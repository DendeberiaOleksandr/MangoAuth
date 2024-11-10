package org.mango.auth.server.service;

public interface MailGeneratorService {
    String generateActivationEmail(String email, String code, String clientName);
}
