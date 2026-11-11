package com.garmentqc.system.controller.superadmin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garmentqc.system.dto.LineProductivityDashboardResponse;
import com.garmentqc.system.enums.Section;
import com.garmentqc.system.exception.BadRequestException;
import com.garmentqc.system.service.LineProductivityDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/super-admin/dashboard/line-productivity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class LineProductivityDashboardSuperAdminController {

    private final LineProductivityDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<LineProductivityDashboardResponse> getDashboard(
            @RequestParam Section section,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (section == null) {
            throw new BadRequestException("Section wajib dipilih");
        }

        LineProductivityDashboardResponse response = dashboardService.getDashboard(
                section,
                startDate,
                endDate);

        return ResponseEntity.ok(response);
    }
}