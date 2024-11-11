package org.mango.auth.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mango.auth.server.enums.EmailEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "email_audit")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EmailAudit {

    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "client_id", nullable = false)
    @ManyToOne
    private Client client;

    @Column(name = "email_from", nullable = false)
    private String emailFrom;

    @Column(name = "email_subject", nullable = false)
    private String emailSubject;

    @Column(name = "email_event", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailEvent emailEvent;

    @Column(name = "email_event_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailEventResult emailEventResult;

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}
