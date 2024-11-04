package org.mango.auth.server.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    @Override
    @Transactional(noRollbackFor = ValidationException.class)
    public TokenResponse generateToken(TokenRequest request){
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return jwtService.generateTokens(user);
    }
}
