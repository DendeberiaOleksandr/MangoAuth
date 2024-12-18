package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.IntrospectTokenResponse;
import org.mango.auth.server.dto.token.RefreshTokenRequest;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.AccountType;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.exception.ExpiredRefreshTokenException;
import org.mango.auth.server.exception.InvalidParameterException;
import org.mango.auth.server.exception.InvalidRefreshTokenException;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.exception.UserIsNotVerifiedException;
import org.mango.auth.server.exception.ValidationException;
import org.mango.auth.server.mapper.RefreshTokenMapper;
import org.mango.auth.server.mapper.TokenMapper;
import org.mango.auth.server.repository.RefreshTokenRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final TokenMapper tokenMapper;
    private final UserDetailsServiceImpl userDetailsService;

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

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser_EmailAndClient_Id(email, clientId);
        if (existingTokenOpt.isPresent()) {
            RefreshToken existingToken = existingTokenOpt.get();

            if (existingToken.getExpiryAt().isAfter(LocalDateTime.now())) {
                return jwtService.generateTokens(user,userEmailAndClientId.getClient(), existingToken);
            } else {
                refreshTokenRepository.delete(existingToken);
            }
        }

        TokenResponse tokenResponse = jwtService.generateTokens(user, userEmailAndClientId.getClient());

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
    public TokenResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if(refreshToken.getExpiryAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        Client client = refreshToken.getClient();

        return jwtService.generateTokens(user, client, refreshToken);
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

    @Override
    public IntrospectTokenResponse introspect(String accessToken, UserDetailsImpl userDetails) {
        if (AccountType.SERVICE.equals(userDetails.getAccountType())) {
            if (!StringUtils.hasText(accessToken)) {
                throw new ValidationException("accessToken is required");
            }
            UserDetailsImpl details = (UserDetailsImpl) userDetailsService.loadByAccessToken(accessToken);
            if (details == null ||!details.getClient().getId().equals(userDetails.getClient().getId())) {
                throw new ValidationException("Invalid accessToken provided");
            }
            userDetails = details;
        }

        return tokenMapper.map(userDetails);
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
