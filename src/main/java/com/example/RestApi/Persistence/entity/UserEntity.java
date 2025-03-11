package com.example.RestApi.Persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String username;
        private String password;
        private String email;
        private int prioridad;

        @Column(name = "disable_timestamp")
        private LocalDateTime disableTimestamp;

        @JsonProperty("isEnabled")
        @Column(name = "is_enabled")
        private boolean isEnabled;

        @Column(name = "account_No_Expired")
        private boolean accountNoExpired;

        @Column(name = "account_No_Locked")
        private boolean accountNoLocked;

        @Column(name = "credential_No_Expired")
        private boolean credentialNoExpired;

        @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
                name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        @Builder.Default
        private Set<RoleEntity> roles = new HashSet<>();
}

