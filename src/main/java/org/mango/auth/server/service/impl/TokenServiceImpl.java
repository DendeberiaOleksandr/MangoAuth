package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.exception.ExpiredRefreshTokenException;
import org.mango.auth.server.exception.InvalidRefreshTokenException;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.exception.UserIsNotVerifiedException;
import org.mango.auth.server.mapper.RefreshTokenMapper;
import org.mango.auth.server.repository.RefreshTokenRepository;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mango.auth.server.util.DateTimeUtils.convertMillisToLocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserClientRoleService userClientRoleService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserService userService;
    private final ClientService clientService;

    @Override
    @Transactional
    public TokenResponse generateToken(TokenRequest request, String userAgent){
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

        LocalDateTime dateTimeIssuedAt = convertMillisToLocalDateTime(tokenResponse.refreshToken().issuedAt());
        LocalDateTime dateTimeExpiresAt = convertMillisToLocalDateTime(tokenResponse.refreshToken().expiresAt());
        saveRefreshToken(
                user,
                userEmailAndClientId.getClient(),
                userAgent,
                tokenResponse.refreshToken().token(),
                dateTimeIssuedAt,
                dateTimeExpiresAt);
        return tokenResponse;
    }

    @Override
    @Transactional
    public TokenResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if(refreshToken.getExpiryAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        return jwtService.generateTokens(user);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String email, UUID clientId) {

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUser_EmailAndClient_Id(email, clientId);

        optionalRefreshToken.ifPresentOrElse(
                refreshTokenRepository::delete,
                () -> {
                    throw new NotFoundException("No refresh token found for the given user and client");
                }
        );
    }

    public void saveRefreshToken(User user,
                                 Client client,
                                 String deviceAgent,
                                 String token,
                                 LocalDateTime issuedAt,
                                 LocalDateTime expiryAt) {
        refreshTokenRepository.findByUserAndClient(user, client)
                .orElseGet(() -> {
                    RefreshToken refreshToken = refreshTokenMapper.map(user, client, deviceAgent, token, issuedAt, expiryAt);
                    return refreshTokenRepository.save(refreshToken);
                });
    }

}
