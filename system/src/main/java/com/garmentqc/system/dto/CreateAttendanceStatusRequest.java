package com.garmentqc.system.dto;

import java.time.LocalDate;

import com.garmentqc.system.enums.AttendanceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttendanceStatusRequest {

    @NotBlank
    private String userId;

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private AttendanceStatus status;

    @NotBlank
    private String reason;
}