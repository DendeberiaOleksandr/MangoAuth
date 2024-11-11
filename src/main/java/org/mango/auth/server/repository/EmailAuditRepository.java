package org.mango.auth.server.repository;

import org.mango.auth.server.entity.EmailAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailAuditRepository extends JpaRepository<EmailAudit, UUID> {}
