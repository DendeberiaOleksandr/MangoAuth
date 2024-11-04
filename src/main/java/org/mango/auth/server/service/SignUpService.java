package org.mango.auth.server.service;

import org.mango.auth.server.dto.SignUp.SignUpRequest;
import org.mango.auth.server.entity.UserClientRole;

public interface SignUpService {

    void signUp(SignUpRequest signUpRequest);

}
