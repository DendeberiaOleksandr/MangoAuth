package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.exception.UserIsNotVerifiedException;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserClientRoleService userClientRoleService;

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

        return jwtService.generateTokens(user);
    }
}
