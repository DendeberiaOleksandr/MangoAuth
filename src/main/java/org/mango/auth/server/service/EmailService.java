package org.mango.auth.server.service;

import org.mango.auth.server.dto.EmailProperties;

public interface EmailService {

    boolean sendEmail(EmailProperties emailProperties);

}
