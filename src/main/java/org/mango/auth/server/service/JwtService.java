package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.User;

public interface JwtService {
    TokenResponse generateTokens(User user);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    String getClientIdFromToken(String token);
}
