
package com.garmentqc.system.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.garmentqc.system.dto.CuttingAnalyticsResponse;
import com.garmentqc.system.dto.CuttingInspectionRequest;
import com.garmentqc.system.dto.CuttingInspectionResponse;
import com.garmentqc.system.enums.CuttingProcessCheck;
import com.garmentqc.system.model.CuttingDefectType;
import com.garmentqc.system.model.User;
import com.garmentqc.system.service.CuttingAnalyticsService;
import com.garmentqc.system.service.CuttingInspectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cutting-inspections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CuttingInspectionAdminController {

    private final CuttingInspectionService cuttingInspectionService;
    private final CuttingAnalyticsService cuttingAnalyticsService;

    @GetMapping("/analytics")
    public ResponseEntity<CuttingAnalyticsResponse> getAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                cuttingAnalyticsService.getAnalytics(
                        startDate,
                        endDate,
                        currentUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CuttingInspectionResponse>> getAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cuttingInspectionService.getAll(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuttingInspectionResponse> getById(@PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cuttingInspectionService.getById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuttingInspectionResponse> update(@PathVariable String id,
            @Valid @RequestBody CuttingInspectionRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cuttingInspectionService.update(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        cuttingInspectionService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<CuttingInspectionResponse> patchComment(@PathVariable String id,
            @RequestBody Map<String, String> request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cuttingInspectionService.patchComment(id, request.get("comment"), currentUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CuttingDefectType>> searchDefectTypes(@RequestParam CuttingProcessCheck processCheck,
            @RequestParam(required = false) String search, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cuttingInspectionService.searchDefectTypes(processCheck, search, currentUser));
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CuttingInspectionResponse>> getAllPaginated(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100));

        return ResponseEntity.ok(
                cuttingInspectionService.getAllPaginated(
                        currentUser, pageable));
    }

}
