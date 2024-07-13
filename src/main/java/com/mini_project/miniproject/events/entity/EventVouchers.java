package com.mini_project.miniproject.events.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_vouchers")
public class EventVouchers {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_vouchers_id_gen")
    @SequenceGenerator(name = "event_vouchers_id_gen", sequenceName = "event_vouchers_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Events event;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "discount_percentage", nullable = false)
    private BigDecimal discountPercentage;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        this.deletedAt = Instant.now();
    }
}
