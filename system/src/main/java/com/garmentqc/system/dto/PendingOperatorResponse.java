package com.garmentqc.system.dto;

import java.time.Instant;
import java.time.LocalDate;

public record PendingOperatorResponse(

        String attendanceId,

        String userId,

        String username,

        String nama,

        String section,

        LocalDate attendanceDate,

        Instant checkInAt) {
}