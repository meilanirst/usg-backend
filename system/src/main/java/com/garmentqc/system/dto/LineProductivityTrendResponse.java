package com.garmentqc.system.dto;

import java.time.LocalDate;

public record LineProductivityTrendResponse(

                LocalDate date,

                Long totalOperators,

                Long totalInspections,

                Long actualQuantity,

                Double effectiveHours,

                Double targetQuantity,

                Double achievementRate) {
}