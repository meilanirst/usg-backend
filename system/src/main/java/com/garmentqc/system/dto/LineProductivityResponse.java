package com.garmentqc.system.dto;

public record LineProductivityResponse(

                String lineId,

                String lineName,

                Long totalOperators,

                Long totalInspections,

                Long actualQuantity,

                Double effectiveHours,

                Double targetQuantity,

                Double achievementRate) {
}