package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;

import java.time.LocalDateTime;

public interface TokenService {
    TokenResponse generateToken(TokenRequest request, String userAgent);
    TokenResponse refreshAccessToken(String refreshTokenValue);
    void revokeRefreshToken(User user, Client client);

}
