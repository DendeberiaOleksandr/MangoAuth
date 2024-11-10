package org.mango.auth.server.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserClientRoleService userClientRoleService;

    @Override
    @Transactional
    public TokenResponse generateToken(TokenRequest request){
        String email = request.email();
        UUID clientId = request.clientId();

        UserClientRole userClientRole = userClientRoleService.findByUserEmailAndClientId(email, clientId);
        if (userClientRole == null) {
            throw new UsernameNotFoundException("User not found for the specified client");
        }

        User user = userClientRole.getUser();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return jwtService.generateTokens(user);
    }
}
