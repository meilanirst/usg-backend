package com.garmentqc.system.dto;

import java.time.Instant;

import com.garmentqc.system.enums.AttendanceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceCorrectionRequest {

    @NotNull
    private AttendanceStatus status;

    private Instant checkInAt;

    private Instant checkOutAt;

    @NotBlank
    private String reason;
}