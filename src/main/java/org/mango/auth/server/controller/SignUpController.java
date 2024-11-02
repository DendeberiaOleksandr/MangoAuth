package org.mango.auth.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.SignUpRequest;
import org.mango.auth.server.service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sign-up")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpService.signUp(request);
        return ResponseEntity.ok().build();
    }

}
