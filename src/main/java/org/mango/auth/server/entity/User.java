package org.mango.auth.server.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mango.auth.server.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column( name = "created_at")
    private LocalDateTime createdAt;

    @Column( name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserClientRole> clientRoles = new ArrayList<>();

    @Column(name = "user_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus = UserStatus.UNVERIFIED;

    @Column(name = "email_verification_code")
    private String emailVerificationCode;

    @Column(name = "email_verification_code_last_sent_at")
    private LocalDateTime emailVerificationCodeLastSentAt;

    @Column(name = "email_verification_code_sent_times")
    private int emailVerificationCodeSentTimes;

    @Column(name = "email_verification_code_last_entered_at")
    private LocalDateTime emailVerificationCodeLastEnteredAt;

    @Column(name = "email_verification_code_entered_times")
    private int emailVerificationCodeEnteredTimes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        userStatus = UserStatus.UNVERIFIED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
