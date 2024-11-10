package org.mango.auth.server.service;

import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;

public interface UserVerificationService {

    void verify(UserVerificationRequest request);

    void sendVerificationEmail(SendUserVerificationEmailRequest request);

}
