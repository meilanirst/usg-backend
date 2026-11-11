
package com.garmentqc.system.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.garmentqc.system.dto.AttendanceCorrectionRequest;
import com.garmentqc.system.dto.AttendanceResponse;
import com.garmentqc.system.dto.CreateAttendanceStatusRequest;
import com.garmentqc.system.enums.Section;
import com.garmentqc.system.service.AttendanceManagementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/attendance")
public class AttendanceManagementAdminController {

        private final AttendanceManagementService attendanceManagementService;

        @GetMapping("/all")
        public List<AttendanceResponse> getAllAttendances(Authentication authentication) {
                return attendanceManagementService.getAllAttendances(authentication.getName());
        }

        @GetMapping("/user/{userId}")
        public List<AttendanceResponse> getAttendancesByUserId(Authentication authentication,
                        @PathVariable String userId) {
                return attendanceManagementService.getAttendancesByUserId(
                                authentication.getName(),
                                userId);
        }

        @GetMapping("/{attendanceId}")
        public AttendanceResponse getAttendanceById(Authentication authentication, @PathVariable String attendanceId) {
                return attendanceManagementService.getAttendanceById(authentication.getName(),
                                attendanceId);
        }

        @GetMapping("/date")
        public List<AttendanceResponse> getAttendancesByDate(Authentication authentication,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
                return attendanceManagementService.getAttendances(
                                authentication.getName(),
                                date != null ? date : LocalDate.now());
        }

        @GetMapping("/section")
        public List<AttendanceResponse> getAttendancesBySection(Authentication authentication,
                        @RequestParam Section section) {
                return attendanceManagementService.getAttendancesBySection(
                                authentication.getName(),
                                section);
        }

        @GetMapping("/section/line")
        public List<AttendanceResponse> getAttendancesBySectionAndLine(Authentication authentication,
                        @RequestParam Section section, @RequestParam String lineId) {
                return attendanceManagementService.getAttendancesBySectionAndLine(
                                authentication.getName(),
                                section,
                                lineId);
        }

        @PutMapping("/{attendanceId}/correction")
        public AttendanceResponse correctAttendance(Authentication authentication, @PathVariable String attendanceId,
                        @Valid @RequestBody AttendanceCorrectionRequest request) {
                return attendanceManagementService.correctAttendance(
                                authentication.getName(),
                                attendanceId,
                                request);
        }

        @PostMapping("/correction")
        public AttendanceResponse createAttendanceStatus(Authentication authentication,
                        @Valid @RequestBody CreateAttendanceStatusRequest request) {
                return attendanceManagementService.createAttendanceStatus(
                                authentication.getName(),
                                request);
        }

        @GetMapping("/all/paginated")
        public ResponseEntity<Page<AttendanceResponse>> getAllAttendancesPaginated(
                        Authentication authentication,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                Math.min(Math.max(size, 1), 100));

                return ResponseEntity.ok(
                                attendanceManagementService.getAllAttendancesPaginated(
                                                authentication.getName(), pageable));
        }

        @GetMapping("/user/{userId}/paginated")
        public ResponseEntity<Page<AttendanceResponse>> getAttendancesByUserIdPaginated(
                        Authentication authentication,
                        @PathVariable String userId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                Math.min(Math.max(size, 1), 100));

                return ResponseEntity.ok(
                                attendanceManagementService.getAttendancesByUserIdPaginated(
                                                authentication.getName(), userId, pageable));
        }

        @GetMapping("/date/paginated")
        public ResponseEntity<Page<AttendanceResponse>> getAttendancesByDatePaginated(
                        Authentication authentication,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                Math.min(Math.max(size, 1), 100));

                return ResponseEntity.ok(
                                attendanceManagementService.getAttendancesPaginated(
                                                authentication.getName(), date, pageable));
        }

        @GetMapping("/section/paginated")
        public ResponseEntity<Page<AttendanceResponse>> getAttendancesBySectionPaginated(
                        Authentication authentication,
                        @RequestParam Section section,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                Math.min(Math.max(size, 1), 100));

                return ResponseEntity.ok(
                                attendanceManagementService.getAttendancesBySectionPaginated(
                                                authentication.getName(), section, pageable));
        }

        @GetMapping("/section/line/paginated")
        public ResponseEntity<Page<AttendanceResponse>> getAttendancesBySectionAndLinePaginated(
                        Authentication authentication,
                        @RequestParam Section section,
                        @RequestParam String lineId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                Math.min(Math.max(size, 1), 100));

                return ResponseEntity.ok(
                                attendanceManagementService.getAttendancesBySectionAndLinePaginated(
                                                authentication.getName(), section, lineId, pageable));
        }

}
