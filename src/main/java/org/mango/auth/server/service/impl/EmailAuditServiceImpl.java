package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.entity.EmailAudit;
import org.mango.auth.server.repository.EmailAuditRepository;
import org.mango.auth.server.service.EmailAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailAuditServiceImpl implements EmailAuditService {

    private final EmailAuditRepository emailAuditRepository;

    @Transactional
    @Override
    public EmailAudit save(EmailAudit emailAudit) {
        return emailAuditRepository.save(emailAudit);
    }
}
