package org.mango.auth.server.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.mango.auth.server.dto.token.TokenDto;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.accessTokenExpiration:3600}")
    private long accessTokenExpiration;

    @Value("${app.token.refreshTokenExpiration:7884000}")
    private long refreshTokenExpiration;

    @Value("${app.token.issuer:http://localhost:8080}")
    private String issuer;

    @Override
    public TokenResponse generateTokens(User user) {
        Date now = new Date();

        Date accessTokenExpDate = new Date(now.getTime() + this.accessTokenExpiration);
        TokenDto accessToken = new TokenDto(
                generateToken(user, now, accessTokenExpDate),
                accessTokenExpDate.getTime(),
                now.getTime());


        Date refreshTokenExpDate = new Date(now.getTime() + this.refreshTokenExpiration);
        TokenDto refreshToken = new TokenDto(
                generateToken(user, now, refreshTokenExpDate),
                refreshTokenExpDate.getTime(),
                now.getTime());


        Role role = user.getClientRoles().stream()
                .findFirst()
                .map(UserClientRole::getRole)
                .orElse(null);

        return new TokenResponse(user.getId(), user.getEmail(), role, accessToken, refreshToken);
    }

    private String generateToken(User user, Date now, Date expiration) {
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
