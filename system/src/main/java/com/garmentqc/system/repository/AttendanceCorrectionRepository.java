package com.garmentqc.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garmentqc.system.model.AttendanceCorrection;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, String> {
    List<AttendanceCorrection> findByAttendance_IdOrderByCreatedAtDesc(String attendanceId);

}