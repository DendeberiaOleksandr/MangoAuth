package org.mango.auth.server.service;

import org.mango.auth.server.dto.EmailCallback;
import org.mango.auth.server.dto.EmailProperties;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<EmailCallback> sendEmail(EmailProperties emailProperties);

}
