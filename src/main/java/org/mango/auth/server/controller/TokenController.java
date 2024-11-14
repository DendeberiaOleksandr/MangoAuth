package org.mango.auth.server.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.token.TokenRequest;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.TokenService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



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
    public ResponseEntity<TokenResponse> refreshAccessToken(
            @RequestParam("refreshToken") String refreshToken) {

        TokenResponse response = tokenService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

//    @PostMapping(ApiPaths.TOKEN_SIGN_OUT)
//    public ResponseEntity<Void> signOut(@AuthenticationPrincipal Jwt principal) {
//
//        UUID userId = UUID.fromString(principal.getClaim("userId"));
//        UUID clientId = UUID.fromString(principal.getClaim("clientId"));
//
//
//        User user = userService.findById(userId);
//        Client client = clientService.findById(clientId);
//
//        tokenService.revokeRefreshToken(user, client);
//        return ResponseEntity.noContent().build();
//    }
}
