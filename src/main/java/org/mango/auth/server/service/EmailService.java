package org.mango.auth.server.service;

import org.mango.auth.server.dto.EmailProperties;

public interface EmailService {

    void sendEmail(EmailProperties emailProperties);

}
