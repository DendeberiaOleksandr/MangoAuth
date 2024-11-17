package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TokenService {
    TokenResponse generateToken(TokenRequest request, String userAgent);
    TokenResponse refreshAccessToken(String refreshTokenValue);
    void revokeRefreshToken(String email, UUID clientId);

}
