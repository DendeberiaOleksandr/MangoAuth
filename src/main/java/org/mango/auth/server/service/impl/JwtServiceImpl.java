package org.mango.auth.server.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.token.TokenDto;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.accessTokenExpiration:3600000}")
    private long accessTokenExpiration;

    @Value("${app.token.refreshTokenExpiration:7884000000}")
    private long refreshTokenExpiration;

    @Value("${app.token.issuer:http://localhost:8080}")
    private String issuer;

    private final UserClientRoleService userClientRoleService;

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> jws = getJwsFromToken(token);
            return jws != null;
        } catch (Exception e) {
            log.debug("Failed to parse JWT", e);
            return false;
        }
    }

    @Override
    public Claims getClaimsFromToken(String token) {
        return getJwsFromToken(token).getPayload();
    }

    @Override
    @Transactional
    public TokenResponse generateTokens(User user, Client client) {
        return generateTokens(user, client, null);
    }

    @Override
    @Transactional
    public TokenResponse generateTokens(User user, Client client, RefreshToken refreshToken) {
        Date now = new Date();
        UserClientRole userClientRole = userClientRoleService.getByUserEmailAndClientId(user.getEmail(), client.getId());

        Date accessTokenExpDate = new Date(now.getTime() + this.accessTokenExpiration);
        TokenDto accessToken = new TokenDto(
                generateToken(user, now, accessTokenExpDate, userClientRole),
                accessTokenExpDate.getTime(),
                now.getTime());

        TokenDto refreshTokenDto = null;

        if (refreshToken == null){
            Date refreshTokenExpDate = new Date(now.getTime() + this.refreshTokenExpiration);
             refreshTokenDto = new TokenDto(
                    generateToken(user, now, refreshTokenExpDate, userClientRole),
                    refreshTokenExpDate.getTime(),
                    now.getTime());
        } else {
            refreshTokenDto = new TokenDto(
                    refreshToken.getToken(),
                    refreshToken.getExpiryAt().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    refreshToken.getIssuedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        }


        return new TokenResponse(user.getId(), user.getEmail(), userClientRole.getRole(), accessToken, refreshTokenDto);
    }

    private String generateToken(User user, Date now, Date expiration, UserClientRole userClientRole) {
        return Jwts.builder()
                .issuer(issuer)
                .claim("CLIENT_ID", userClientRole.getClient().getId())
                .claim("ROLE", userClientRole.getRole().name())
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Jws<Claims> getJwsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
