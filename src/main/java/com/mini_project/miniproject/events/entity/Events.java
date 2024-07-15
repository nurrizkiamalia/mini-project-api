package com.mini_project.miniproject.events.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_id_gen")
    @SequenceGenerator(name = "events_id_gen", sequenceName = "events_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "organizer_id", nullable = false)
//    private Long organizerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Users organizer;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "location", length = 255, nullable = false)
    private String location;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "event_type", length = 10)
    private String eventType;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "referral_quota")
    private Integer referralQuota;

    @Column(name = "event_picture", length = 100)
    private String eventPicture;

    @JsonManagedReference
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TicketTiers> ticketTiers;

    @JsonManagedReference
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EventVouchers> eventVouchers;

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
