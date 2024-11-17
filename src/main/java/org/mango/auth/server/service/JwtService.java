package org.mango.auth.server.service;

import io.jsonwebtoken.Claims;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;

public interface JwtService {
    TokenResponse generateTokens(User user, Client client);
    TokenResponse generateTokens(User user, Client client, RefreshToken refreshToken);
    boolean validateToken(String token);
    Claims getClaimsFromToken(String token);
}
