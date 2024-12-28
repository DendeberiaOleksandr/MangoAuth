package org.mango.auth.server.service;

import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.service.impl.JwtServiceImpl;

public interface JwtService {
    TokenResponse generateTokens(User user, Client client, Role role);
    TokenResponse generateTokens(User user, Client client, Role role, RefreshToken refreshToken);
    boolean validateToken(String token);
    boolean validateToken(String token, Client client);
    JwtServiceImpl.TokenPayload getTokenPayload(String token);
}
