package com.mini_project.miniproject.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mini_project.miniproject.events.entity.Events;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Size(max = 20)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Size(max = 50)
    @NotNull
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @JsonIgnore
    @Size(max = 100)
    @NotNull
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Size(max = 100)
    @NotNull
    @Column(name = "referral_code", nullable = false, length = 100)
    private String referralCode;

    @Size(max = 100)
//    @NotNull
//    @Column(name = "avatar", nullable = false, length = 100)
    @Column(name = "avatar", length = 100)
    private String avatar;

//    @Size(max = 255)
//    @NotNull
//    @Column(name = "quotes", nullable = false, length = 255)
    @Column(name = "quotes")
    private String quotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

//    @OneToMany(mappedBy = "users")
//    private List<Events> organizedEvents = new ArrayList<>();


    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
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

    public void updateProfile(String firstName, String lastName, String quotes, String avatar) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.quotes = quotes;
        this.avatar = avatar;
    }

}
