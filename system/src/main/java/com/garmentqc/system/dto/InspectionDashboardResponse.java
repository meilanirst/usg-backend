
package com.garmentqc.system.dto;

import java.time.LocalDate;
import java.util.List;

public record InspectionDashboardResponse(

                LocalDate startDate,

                LocalDate endDate,

                LocalDate previousStartDate,

                LocalDate previousEndDate,

                List<InspectionTrendPoint> inspectionTrend,

                List<DefectTrendPoint> defectTrend,

                List<BuyerDefectPoint> buyerDefects,

                List<BuyerDefectTrendPoint> buyerDefectTrend,

                List<BuyerStyleDefectPoint> buyerStyleDefects,

                List<BuyerStyleDefectTrendPoint> buyerStyleDefectTrend,

                List<TopDefectPoint> topDefects,

                PeriodComparison periodComparison,

                Growth growth

) {

        public record InspectionTrendPoint(

                        LocalDate date,

                        long totalInspections,

                        long totalQuantityInspect) {
        }

        public record DefectTrendPoint(

                        LocalDate date,

                        long totalDefects,

                        double defectPercentage) {
        }

        public record BuyerDefectPoint(

                        String buyerName,

                        long totalDefects,

                        double percentage) {
        }

        public record BuyerDefectTrendPoint(

                        LocalDate date,

                        String buyerName,

                        long totalDefects) {
        }

        public record BuyerStyleDefectPoint(

                        String buyerName,

                        String styleCode,

                        long totalDefects,

                        double percentage) {
        }

        public record BuyerStyleDefectTrendPoint(

                        LocalDate date,

                        String buyerName,

                        String styleCode,

                        long totalDefects) {
        }

        public record TopDefectPoint(

                        String defectName,

                        String category,

                        String area,

                        long totalDefects,

                        double percentage) {
        }

        public record PeriodSummary(

                        long totalInspections,

                        long totalQuantityInspect,

                        long totalDefects,

                        double defectPercentage) {
        }

        public record PeriodComparison(

                        PeriodSummary currentPeriod,

                        PeriodSummary previousPeriod) {
        }

        public record Growth(

                        Double inspectionGrowth,

                        Double quantityInspectGrowth,

                        Double defectGrowth,

                        Double defectRateChangePercentagePoints) {
        }
}
