package com.garmentqc.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garmentqc.system.model.OperatorLineAssignment;

public interface OperatorLineAssignmentRepository extends JpaRepository<OperatorLineAssignment, String> {

        Optional<OperatorLineAssignment> findByAttendance_IdAndEndAtIsNull(String attendanceId);

        Optional<OperatorLineAssignment> findByUser_IdAndAttendance_AttendanceDateAndEndAtIsNull(String userId,
                        LocalDate attendanceDate);

        List<OperatorLineAssignment> findByAttendance_IdOrderByStartAtAsc(String attendanceId);

        List<OperatorLineAssignment> findByAttendance_AttendanceDateOrderByStartAtAsc(LocalDate date);

        List<OperatorLineAssignment> findByLine_IdAndAttendance_AttendanceDate(String lineId, LocalDate date);

        boolean existsByAttendance_IdAndEndAtIsNull(String attendanceId);

        List<OperatorLineAssignment> findByAttendance_AttendanceDateBetweenOrderByStartAtAsc(LocalDate startDate,
                        LocalDate endDate);

        Optional<OperatorLineAssignment> findFirstByAttendance_IdOrderByStartAtAsc(String attendanceId);

        List<OperatorLineAssignment> findByLine_Id(String lineId);

        List<OperatorLineAssignment> findByAttendance_Id(String attendanceId);

}