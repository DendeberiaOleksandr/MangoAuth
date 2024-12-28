package org.mango.auth.server.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.token.TokenDto;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    @Value("${app.token.issuer:http://localhost:8080}")
    private String issuer;

    private final ClientService clientService;

    private final ObjectMapper objectMapper;

    @Override
    public boolean validateToken(String token) {
        TokenPayload tokenPayload = null;
        try {
            tokenPayload = getTokenPayload(token);
        } catch (IllegalArgumentException e) {
            return false;
        }
        Client client = clientService.getById(tokenPayload.clientId);
        return validateToken(token, client);
    }

    @Override
    public boolean validateToken(String token, Client client) {
        RSAPublicKey rsaPublicKey = KeyServiceImpl.decodeBase64ToRSAPublicKey(client.getPublicKey());

        try {
            Jwts
                    .parser()
                    .verifyWith(rsaPublicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TokenPayload getTokenPayload(String token) {
        if (StringUtils.hasText(token)) {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length == 3) {
                String encodedPayload = tokenParts[1];
                byte[] decoded = Base64.getDecoder().decode(encodedPayload);
                try {
                    return objectMapper.readValue(decoded, TokenPayload.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("Invalid JWT provided: %s".formatted(token));
    }

    @Override
    @Transactional
    public TokenResponse generateTokens(User user, Client client, Role role) {
        return generateTokens(user, client, role, null);
    }

    @Override
    @Transactional
    public TokenResponse generateTokens(User user, Client client, Role role, RefreshToken refreshToken) {
        Date now = new Date();

        Date accessTokenExpDate = new Date(now.getTime() + this.accessTokenExpiration);
        TokenDto accessToken = new TokenDto(
                generateToken(user, now, accessTokenExpDate, client, role),
                accessTokenExpDate.getTime(),
                now.getTime());

        TokenDto refreshTokenDto = null;

        if (refreshToken == null){
            Date refreshTokenExpDate = new Date(now.getTime() + this.refreshTokenExpiration);
             refreshTokenDto = new TokenDto(
                    generateToken(user, now, refreshTokenExpDate, client, role),
                    refreshTokenExpDate.getTime(),
                    now.getTime());
        } else {
            refreshTokenDto = new TokenDto(
                    refreshToken.getToken(),
                    refreshToken.getExpiryAt().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    refreshToken.getIssuedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        }


        return new TokenResponse(user.getId(), user.getEmail(), role, accessToken, refreshTokenDto);
    }

    private String generateToken(User user, Date now, Date expiration, Client client, Role role) {
        try {
            return Jwts.builder()
                    .issuer(issuer)
                    .claim("clientId", client.getId())
                    .claim("role", role.name())
                    .subject(user.getEmail())
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(KeyServiceImpl.decodeBase64ToRSAPrivateKey(client.getPrivateKey()))
                    .compact();
        } catch (Exception e) {
            throw e;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenPayload {
        private UUID clientId;
        private String role;
        @JsonProperty("sub")
        private String userEmail;
    }

    @Value("${app.token.accessTokenExpiration:3600000}")
    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Value("${app.token.refreshTokenExpiration:7884000000}")
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
