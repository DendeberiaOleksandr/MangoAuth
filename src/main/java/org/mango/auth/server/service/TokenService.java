package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.RefreshTokenRequest;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;

import java.util.UUID;

public interface TokenService {
    TokenResponse generateToken(TokenRequest request, String userAgent);
    TokenResponse refreshAccessToken(RefreshTokenRequest request);
    void revokeRefreshToken(String email, UUID clientId);
}
