
package com.garmentqc.system.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CuttingAnalyticsResponse {

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate previousStartDate;

    private LocalDate previousEndDate;

    private List<DailyData> defectPercentageChart;

    private List<DailyData> totalInspectChart;

    private List<DailyData> totalDefectChart;

    private MetricSummary defectPercentageSummary;

    private MetricSummary totalInspectSummary;

    private MetricSummary totalDefectSummary;

    @Getter
    @AllArgsConstructor
    public static class DailyData {

        private LocalDate date;

        private double value;
    }

    @Getter
    @AllArgsConstructor
    public static class MetricSummary {

        private double currentValue;

        private double previousValue;

        private double difference;

        private Double percentageChange;
    }
}
