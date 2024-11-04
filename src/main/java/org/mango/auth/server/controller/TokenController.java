package org.mango.auth.server.controller;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.TOKEN)
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request) {
        TokenResponse response = tokenService.generateToken(request);
        return ResponseEntity.ok(response);
    }
}
