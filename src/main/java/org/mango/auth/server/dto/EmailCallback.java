package org.mango.auth.server.dto;

public record EmailCallback(String emailFrom, String subject, boolean isSent) {}
