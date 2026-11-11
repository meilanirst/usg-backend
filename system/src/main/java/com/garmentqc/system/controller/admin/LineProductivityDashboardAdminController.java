package com.garmentqc.system.controller.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garmentqc.system.dto.LineProductivityDashboardResponse;
import com.garmentqc.system.exception.UnauthorizedException;
import com.garmentqc.system.model.User;
import com.garmentqc.system.repository.UserRepository;
import com.garmentqc.system.service.LineProductivityDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/line-productivity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LineProductivityDashboardAdminController {

    private final LineProductivityDashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<LineProductivityDashboardResponse> getDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User tidak ditemukan"));

        if (!currentUser.isActive()) {
            throw new UnauthorizedException("User tidak aktif");
        }

        if (currentUser.getSection() == null) {
            throw new UnauthorizedException("Admin belum memiliki section");
        }

        LineProductivityDashboardResponse response = dashboardService.getDashboard(
                currentUser.getSection(),
                startDate,
                endDate);

        return ResponseEntity.ok(response);
    }
    
}