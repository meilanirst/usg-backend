
package com.garmentqc.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.garmentqc.system.enums.Role;
import com.garmentqc.system.enums.Section;
import com.garmentqc.system.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    Optional<Attendance> findByUser_IdAndAttendanceDate(String userId, LocalDate attendanceDate);

    boolean existsByUser_IdAndAttendanceDate(String userId, LocalDate attendanceDate);

    Optional<Attendance> findByUser_IdAndAttendanceDateAndCheckOutAtIsNull(String userId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByUser_Id(String userId);

    @Query(value = """
            SELECT a FROM Attendance a
            WHERE a.user.role = :role
            AND (:section IS NULL OR a.user.section = :section)
            ORDER BY a.attendanceDate DESC, a.id DESC
            """, countQuery = """
            SELECT COUNT(a) FROM Attendance a
            WHERE a.user.role = :role
            AND (:section IS NULL OR a.user.section = :section)
            """)
    Page<Attendance> findQcAttendances(
            @Param("role") Role role,
            @Param("section") Section section,
            Pageable pageable);

    @Query(value = """
            SELECT a FROM Attendance a
            WHERE a.user.role = :role
            AND (:section IS NULL OR a.user.section = :section)
            AND a.attendanceDate = :date
            ORDER BY a.id DESC
            """, countQuery = """
            SELECT COUNT(a) FROM Attendance a
            WHERE a.user.role = :role
            AND (:section IS NULL OR a.user.section = :section)
            AND a.attendanceDate = :date
            """)
    Page<Attendance> findQcAttendancesByDate(
            @Param("role") Role role,
            @Param("section") Section section,
            @Param("date") LocalDate date,
            Pageable pageable);

    @Query(value = """
            SELECT a FROM Attendance a
            WHERE a.user.id = :userId
            ORDER BY a.attendanceDate DESC, a.id DESC
            """, countQuery = """
            SELECT COUNT(a) FROM Attendance a
            WHERE a.user.id = :userId
            """)
    Page<Attendance> findAttendancesByUserId(
            @Param("userId") String userId,
            Pageable pageable);

    @Query(value = """
            SELECT a FROM Attendance a
            WHERE a.user.role = :role
            AND a.user.section = :section
            ORDER BY a.attendanceDate DESC, a.id DESC
            """, countQuery = """
            SELECT COUNT(a) FROM Attendance a
            WHERE a.user.role = :role
            AND a.user.section = :section
            """)
    Page<Attendance> findQcAttendancesBySection(
            @Param("role") Role role,
            @Param("section") Section section,
            Pageable pageable);

    @Query(value = """
            SELECT a FROM Attendance a
            WHERE a.user.role = :role
            AND a.user.section = :section
            AND EXISTS (
                SELECT ola.id
                FROM OperatorLineAssignment ola
                WHERE ola.attendance = a
                AND ola.line.id = :lineId
            )
            ORDER BY a.attendanceDate DESC, a.id DESC
            """, countQuery = """
            SELECT COUNT(a) FROM Attendance a
            WHERE a.user.role = :role
            AND a.user.section = :section
            AND EXISTS (
                SELECT ola.id
                FROM OperatorLineAssignment ola
                WHERE ola.attendance = a
                AND ola.line.id = :lineId
            )
            """)
    Page<Attendance> findQcAttendancesBySectionAndLine(
            @Param("role") Role role,
            @Param("section") Section section,
            @Param("lineId") String lineId,
            Pageable pageable);
}
