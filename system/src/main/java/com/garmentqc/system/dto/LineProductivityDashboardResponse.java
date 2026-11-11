package com.garmentqc.system.dto;

import java.time.LocalDate;
import java.util.List;

import com.garmentqc.system.enums.Section;

public record LineProductivityDashboardResponse(

                LocalDate startDate,

                LocalDate endDate,

                Section section,

                Integer targetPerHour,

                Long totalLines,

                Long totalOperators,

                Long totalInspections,

                Long totalActualQuantity,

                Double totalTargetQuantity,

                Double overallAchievementRate,

                List<LineProductivityResponse> lines,

                List<LineProductivityTrendResponse> dailyTrends) {
}