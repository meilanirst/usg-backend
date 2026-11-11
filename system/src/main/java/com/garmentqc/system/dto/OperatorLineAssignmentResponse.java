package com.garmentqc.system.dto;

import java.time.Instant;
import java.time.LocalDate;

public record OperatorLineAssignmentResponse(

                String assignmentId,

                String attendanceId,

                String userId,

                String username,

                String nama,

                String section,

                LocalDate attendanceDate,

                String lineId,

                String lineName,

                Instant startAt,

                Instant endAt,

                boolean active) {
}