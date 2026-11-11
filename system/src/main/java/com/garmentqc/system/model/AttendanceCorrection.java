package com.garmentqc.system.model;

import java.time.Instant;

import com.garmentqc.system.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_correction")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceCorrection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private AttendanceStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private AttendanceStatus newStatus;

    @Column(name = "previous_check_in_at")
    private Instant previousCheckInAt;

    @Column(name = "new_check_in_at")
    private Instant newCheckInAt;

    @Column(name = "previous_check_out_at")
    private Instant previousCheckOutAt;

    @Column(name = "new_check_out_at")
    private Instant newCheckOutAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corrected_by", nullable = false)
    private User correctedBy;

    @Column(name = "corrected_at", nullable = false)
    private Instant correctedAt;

}
