package org.mango.auth.server.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.RefreshTokenRequest;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@Validated
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final UserService userService;
    private final ClientService clientService;

    @PostMapping(ApiPaths.TOKEN)
    public ResponseEntity<TokenResponse> generateToken(@Valid
            @RequestBody TokenRequest request,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent) {
        TokenResponse response = tokenService.generateToken(request, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiPaths.TOKEN_REFRESH)
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        TokenResponse response = tokenService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(ApiPaths.TOKEN_SIGN_OUT)
    public ResponseEntity<Void> signOut(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String email = principal.getEmail();
        UUID clientId = principal.getClient().getId();

        tokenService.revokeRefreshToken(email, clientId);
        return ResponseEntity.noContent().build();
    }
}
