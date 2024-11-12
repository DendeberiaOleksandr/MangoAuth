package org.mango.auth.server.dto;

import org.mango.auth.server.entity.EmailEventResult;

public record EmailCallback(String emailFrom, String subject, EmailEventResult emailEventResult) {}
