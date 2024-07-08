package com.mini_project.miniproject.events.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "referral_promo")
public class ReferralPromo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "referral_promo_id_gen")
    @SequenceGenerator(name = "referral_promo_id_gen", sequenceName = "referral_promo_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "event_id", nullable = false)
//    private Long eventId;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Events event;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

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
