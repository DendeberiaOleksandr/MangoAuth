package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;

public interface TokenService {
    TokenResponse generateToken(TokenRequest request);
}
