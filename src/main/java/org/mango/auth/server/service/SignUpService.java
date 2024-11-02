package org.mango.auth.server.service;

import org.mango.auth.server.dto.SignUpRequest;

public interface SignUpService {

    void signUp(SignUpRequest signUpRequest);

}
