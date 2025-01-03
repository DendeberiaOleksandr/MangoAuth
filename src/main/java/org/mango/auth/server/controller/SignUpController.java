package org.mango.auth.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.SignUp.SignUpRequest;
import org.mango.auth.server.service.SignUpService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.SIGN_UP)
@Validated
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpService.signUp(request);
        return ResponseEntity.ok().build();
    }

}
