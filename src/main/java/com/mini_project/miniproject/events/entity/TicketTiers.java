package com.mini_project.miniproject.events.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_tiers")
public class TicketTiers {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_tiers_id_gen")
    @SequenceGenerator(name = "ticket_tiers_id_gen", sequenceName = "ticket_tiers_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Events event;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

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
