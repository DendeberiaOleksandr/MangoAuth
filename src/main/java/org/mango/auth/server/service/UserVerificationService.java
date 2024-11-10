package org.mango.auth.server.service;

import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;
import org.mango.auth.server.entity.UserClientRole;

public interface UserVerificationService {

    void verify(UserVerificationRequest request);

    void sendVerificationEmail(SendUserVerificationEmailRequest request);
    void sendVerificationEmail(UserClientRole userClientRole);

}
