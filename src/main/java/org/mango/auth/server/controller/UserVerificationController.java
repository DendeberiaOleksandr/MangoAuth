package org.mango.auth.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;
import org.mango.auth.server.service.UserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mango.auth.server.util.ApiPaths.USER_API;

@RestController
@RequestMapping(USER_API)
@RequiredArgsConstructor
@Validated
public class UserVerificationController {

    private final UserVerificationService service;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid UserVerificationRequest request) {
        service.verify(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody @Valid SendUserVerificationEmailRequest request) {
        service.sendVerificationEmail(request);
        return ResponseEntity.ok().build();
    }

}
