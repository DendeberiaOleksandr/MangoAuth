package org.mango.auth.server.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.exception.UserIsNotVerifiedException;
import org.mango.auth.server.repository.RefreshTokenRepository;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserClientRoleService userClientRoleService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpServletRequest httpServletRequest;


    @Override
    @Transactional
    public TokenResponse generateToken(TokenRequest request){
        String email = request.email();
        UUID clientId = request.clientId();

        UserClientRole userEmailAndClientId = userClientRoleService.getByUserEmailAndClientId(email, clientId);

        User user = userEmailAndClientId.getUser();

        if (UserStatus.UNVERIFIED.equals(user.getUserStatus())) {
            throw new UserIsNotVerifiedException("User: %s is not verified in client: %s".formatted(email, clientId.toString()));
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        TokenResponse tokenResponse = jwtService.generateTokens(user);
//----------------------------------------------------------------------------------------------------------------------
        String deviceAgent = httpServletRequest.getHeader("User-Agent");
        long issuedAt = tokenResponse.refreshToken().issuedAt();
        LocalDateTime dateTimeIssuedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(issuedAt), ZoneId.systemDefault());
        long expiresAt = tokenResponse.refreshToken().expiresAt();
        LocalDateTime dateTimeExpiresAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt), ZoneId.systemDefault());
        saveOrRetrieveRefreshToken(
                user,
                userEmailAndClientId.getClient(),
                deviceAgent,
                tokenResponse.refreshToken().token(),
                dateTimeIssuedAt,
                dateTimeExpiresAt
        );
//----------------------------------------------------------------------------------------------------------------------
        return tokenResponse;
    }

    @Override
    @Transactional
    public TokenResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if(refreshToken.getExpiryAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new BadCredentialsException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        return jwtService.generateTokens(user);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(User user, Client client) {
        refreshTokenRepository.findByUserAndClient(user, client)
                .ifPresent(refreshTokenRepository::delete);
    }


    public void saveOrRetrieveRefreshToken(User user,
                                           Client client,
                                           String deviceAgent,
                                           String token,
                                           LocalDateTime issuedAt,
                                           LocalDateTime expiryAt) {
        refreshTokenRepository.findByUserAndClient(user, client)
                .orElseGet(() -> {
                    RefreshToken refreshToken = new RefreshToken();
                    refreshToken.setUser(user);
                    refreshToken.setClient(client);
                    refreshToken.setDeviceAgent(deviceAgent);
                    refreshToken.setToken(token);
                    refreshToken.setIssuedAt(issuedAt);
                    refreshToken.setExpiryAt(expiryAt);

                    return refreshTokenRepository.save(refreshToken);
                });
    }
}
