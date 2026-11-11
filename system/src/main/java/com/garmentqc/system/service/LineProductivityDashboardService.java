
package com.garmentqc.system.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garmentqc.system.dto.LineProductivityDashboardResponse;
import com.garmentqc.system.dto.LineProductivityResponse;
import com.garmentqc.system.dto.LineProductivityTrendResponse;
import com.garmentqc.system.enums.Section;
import com.garmentqc.system.exception.BadRequestException;
import com.garmentqc.system.model.Attendance;
import com.garmentqc.system.model.Inspection;
import com.garmentqc.system.model.Line;
import com.garmentqc.system.model.OperatorLineAssignment;
import com.garmentqc.system.repository.InspectionRepository;
import com.garmentqc.system.repository.LineInspectionTotalProjection;
import com.garmentqc.system.repository.LineRepository;
import com.garmentqc.system.repository.OperatorLineAssignmentRepository;
import com.garmentqc.system.repository.SectionProductivityTargetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LineProductivityDashboardService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");
    private static final int DEFAULT_TARGET_PER_HOUR = 40;

    private final LineRepository lineRepository;
    private final OperatorLineAssignmentRepository assignmentRepository;
    private final InspectionRepository inspectionRepository;
    private final SectionProductivityTargetRepository targetRepository;

    @Transactional(readOnly = true)
    public LineProductivityDashboardResponse getDashboard(Section section, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);
        if (section == null) {
            throw new BadRequestException("Section wajib dipilih");
        }

        if (section == Section.CUTTING) {
            throw new BadRequestException(
                    "Data produktivitas tidak tersedia untuk section CUTTING");
        }

        LocalDate today = LocalDate.now(ZONE);

        if (endDate.isAfter(today)) {
            throw new BadRequestException("End date tidak boleh di masa depan");
        }

        Instant queryStart = startDate
                .atStartOfDay(ZONE)
                .toInstant();

        Instant queryEnd = endDate
                .plusDays(1)
                .atStartOfDay(ZONE)
                .toInstant();

        Instant now = Instant.now();

        int targetPerHour = section == Section.PACKING
                ? 0
                : targetRepository.findBySectionAndActiveTrue(section)
                        .map(target -> target.getTargetPerHour())
                        .orElse(DEFAULT_TARGET_PER_HOUR);

        List<Line> lines = lineRepository
                .findBySectionAndActiveTrue(section);

        List<OperatorLineAssignment> assignments = assignmentRepository
                .findByAttendance_AttendanceDateBetweenOrderByStartAtAsc(
                        startDate,
                        endDate);

        List<LineInspectionTotalProjection> inspectionTotals = inspectionRepository.findLineInspectionTotals(
                queryStart,
                queryEnd,
                section);

        List<Inspection> trendInspections = inspectionRepository.findDashboardTrendInspections(
                queryStart,
                queryEnd);

        Map<String, LineData> lineDataMap = new HashMap<>();

        for (Line line : lines) {
            lineDataMap.put(line.getId(), new LineData());
        }
        Map<String, LineInspectionTotalProjection> inspectionMap = new HashMap<>();

        for (LineInspectionTotalProjection projection : inspectionTotals) {
            inspectionMap.put(projection.getLineId(), projection);
        }

        Map<LocalDate, DailyData> dailyDataMap = new HashMap<>();

        startDate.datesUntil(endDate.plusDays(1))
                .forEach(date -> dailyDataMap.put(date, new DailyData()));

        Set<String> allOperatorIds = new HashSet<>();

        Map<LocalDate, Map<String, OperatorDailyData>> operatorDailyMap = new HashMap<>();
        for (Inspection inspection : trendInspections) {

            if (inspection.getLine() == null
                    || inspection.getLine().getSection() != section
                    || inspection.getCreatedAt() == null) {
                continue;
            }

            LocalDate inspectionDate = inspection.getCreatedAt()
                    .atZone(ZONE)
                    .toLocalDate();

            DailyData daily = dailyDataMap.get(inspectionDate);

            if (daily == null) {
                continue;
            }

            daily.totalInspections++;

            daily.actualQuantity += safeInteger(
                    inspection.getInspectQuantity());
        }

        for (OperatorLineAssignment assignment : assignments) {

            Line line = assignment.getLine();

            if (line == null) {
                continue;
            }

            LineData lineData = lineDataMap.get(line.getId());

            if (lineData == null) {
                continue;
            }

            Attendance attendance = assignment.getAttendance();

            if (attendance == null) {
                continue;
            }

            LocalDate attendanceDate = attendance.getAttendanceDate();

            if (attendanceDate == null) {
                continue;
            }

            DailyData daily = dailyDataMap.get(attendanceDate);

            if (daily == null) {
                continue;
            }

            Instant checkIn = attendance.getCheckInAt();

            if (checkIn == null) {
                continue;
            }

            if (attendance.getCheckOutAt() == null
                    && !attendanceDate.equals(today)) {
                continue;
            }

            Instant attendanceEnd = attendance.getCheckOutAt() != null
                    ? attendance.getCheckOutAt()
                    : now;

            Instant assignmentStart = assignment.getStartAt();

            if (assignmentStart == null) {
                continue;
            }

            Instant assignmentEnd = assignment.getEndAt() != null
                    ? assignment.getEndAt()
                    : attendanceEnd;

            if (assignmentStart.isBefore(checkIn)) {
                assignmentStart = checkIn;
            }

            if (assignmentEnd.isAfter(attendanceEnd)) {
                assignmentEnd = attendanceEnd;
            }

            if (!assignmentEnd.isAfter(assignmentStart)) {
                continue;
            }

            long workingMinutes = ChronoUnit.MINUTES.between(
                    checkIn,
                    attendanceEnd);

            if (workingMinutes <= 0) {
                continue;
            }

            long effectiveMinutes;

            if (attendance.getCheckOutAt() != null
                    && attendance.getEffectiveMinutes() != null) {

                effectiveMinutes = attendance.getEffectiveMinutes();

            } else {
                int breakMinutes = calculateBreakMinutes(workingMinutes);

                effectiveMinutes = workingMinutes - breakMinutes;
            }

            if (effectiveMinutes < 0) {
                effectiveMinutes = 0;
            }

            double effectiveRatio = Math.min(
                    1.0,
                    (double) effectiveMinutes / workingMinutes);

            long assignmentSeconds = ChronoUnit.SECONDS.between(
                    assignmentStart,
                    assignmentEnd);

            double effectiveAssignmentMinutes = assignmentSeconds / 60.0 * effectiveRatio;

            if (effectiveAssignmentMinutes <= 0) {
                continue;
            }

            if (assignment.getUser() == null
                    || assignment.getUser().getId() == null) {
                continue;
            }

            String operatorId = assignment.getUser().getId();

            lineData.effectiveMinutes += effectiveAssignmentMinutes;
            lineData.operatorIds.add(operatorId);

            allOperatorIds.add(operatorId);

            daily.effectiveMinutes += effectiveAssignmentMinutes;
            daily.operatorIds.add(operatorId);

            Map<String, OperatorDailyData> operatorMap = operatorDailyMap.computeIfAbsent(
                    attendanceDate,
                    key -> new HashMap<>());

            OperatorDailyData operatorData = operatorMap.computeIfAbsent(
                    operatorId,
                    key -> new OperatorDailyData());

            operatorData.lineEffectiveMinutes.merge(
                    line.getId(),
                    effectiveAssignmentMinutes,
                    Double::sum);
        }

        for (Map.Entry<LocalDate, Map<String, OperatorDailyData>> dateEntry : operatorDailyMap.entrySet()) {
            DailyData daily = dailyDataMap.get(dateEntry.getKey());

            if (daily == null) {
                continue;
            }

            for (OperatorDailyData operatorData : dateEntry.getValue().values()) {

                double totalEffectiveMinutes = operatorData.lineEffectiveMinutes.values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

                if (totalEffectiveMinutes <= 0 || targetPerHour <= 0) {
                    continue;
                }

                long completedHours = (long) Math.floor(
                        (totalEffectiveMinutes + 0.000001) / 60.0);

                double operatorTarget = completedHours * (double) targetPerHour;

                if (operatorTarget <= 0) {
                    continue;
                }

                daily.targetQuantity += operatorTarget;

                for (Map.Entry<String, Double> lineEntry : operatorData.lineEffectiveMinutes.entrySet()) {
                    String lineId = lineEntry.getKey();
                    double lineEffectiveMinutes = lineEntry.getValue();
                    double lineTarget = operatorTarget * (lineEffectiveMinutes / totalEffectiveMinutes);
                    LineData lineData = lineDataMap.get(lineId);

                    if (lineData != null) {
                        lineData.targetQuantity += lineTarget;
                    }
                }
            }
        }

        List<LineProductivityResponse> lineResponses = new ArrayList<>();

        long totalInspections = 0;
        long totalActualQuantity = 0;
        double totalTargetQuantity = 0;

        for (Line line : lines) {

            LineData data = lineDataMap.get(line.getId());

            LineInspectionTotalProjection inspection = inspectionMap.get(line.getId());

            long lineTotalInspections = inspection != null
                    ? safeLong(inspection.getTotalInspections())
                    : 0L;

            long actualQuantity = inspection != null
                    ? safeLong(inspection.getActualQuantity())
                    : 0L;

            double effectiveHours = data.effectiveMinutes / 60.0;

            double targetQuantity = data.targetQuantity;

            Double achievementRate = targetQuantity > 0
                    ? round2(actualQuantity * 100.0 / targetQuantity)
                    : null;

            lineResponses.add(new LineProductivityResponse(
                    line.getId(),
                    line.getName(),
                    (long) data.operatorIds.size(),
                    lineTotalInspections,
                    actualQuantity,
                    round2(effectiveHours),
                    round2(targetQuantity),
                    achievementRate));

            totalInspections += lineTotalInspections;
            totalActualQuantity += actualQuantity;
            totalTargetQuantity += targetQuantity;
        }

        List<LineProductivityTrendResponse> dailyTrends = new ArrayList<>();

        for (LocalDate date : startDate.datesUntil(endDate.plusDays(1)).toList()) {

            DailyData daily = dailyDataMap.get(date);

            double effectiveHours = daily.effectiveMinutes / 60.0;

            double targetQuantity = daily.targetQuantity;

            Double achievementRate = targetQuantity > 0
                    ? round2(daily.actualQuantity * 100.0 / targetQuantity)
                    : null;

            dailyTrends.add(new LineProductivityTrendResponse(
                    date,
                    (long) daily.operatorIds.size(),
                    daily.totalInspections,
                    daily.actualQuantity,
                    round2(effectiveHours),
                    round2(targetQuantity),
                    achievementRate));
        }

        Double overallAchievementRate = totalTargetQuantity > 0
                ? round2(totalActualQuantity * 100.0 / totalTargetQuantity)
                : null;

        return new LineProductivityDashboardResponse(
                startDate,
                endDate,
                section,
                targetPerHour,
                (long) lines.size(),
                (long) allOperatorIds.size(),
                totalInspections,
                totalActualQuantity,
                round2(totalTargetQuantity),
                overallAchievementRate,
                lineResponses,
                dailyTrends);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date dan end date wajib diisi");
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date tidak boleh setelah end date");
        }
    }

    private int calculateBreakMinutes(long workingMinutes) {

        if (workingMinutes < 570) {
            return 0;
        }

        if (workingMinutes < 600) {
            return 30;
        }

        return 60;
    }

    private long safeLong(Long value) {
        return value != null ? value : 0L;
    }

    private long safeInteger(Integer value) {
        return value != null ? value.longValue() : 0L;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class LineData {
        private final Set<String> operatorIds = new HashSet<>();
        private double effectiveMinutes;
        private double targetQuantity;
    }

    private static class DailyData {
        private final Set<String> operatorIds = new HashSet<>();
        private long totalInspections;
        private long actualQuantity;
        private double effectiveMinutes;
        private double targetQuantity;
    }

    private static class OperatorDailyData {
        private final Map<String, Double> lineEffectiveMinutes = new HashMap<>();
    }
}
