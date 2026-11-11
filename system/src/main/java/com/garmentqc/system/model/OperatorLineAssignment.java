package com.garmentqc.system.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operator_line_assignments", indexes = {
        @Index(name = "idx_assignment_attendance", columnList = "attendance_id"),
        @Index(name = "idx_assignment_line_start", columnList = "line_id, start_at"),
        @Index(name = "idx_assignment_user_start", columnList = "user_id, start_at")
})
@Getter
@Setter
@NoArgsConstructor
public class OperatorLineAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;
}
