package org.mango.auth.server.controller;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.user.UserLightDto;
import org.mango.auth.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.mango.auth.server.util.ApiPaths.USER_API;

@RestController
@RequestMapping(USER_API)
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserLightDto>> searchUsers(@RequestParam("clientId") UUID clientId, Pageable pageable) {
        return ResponseEntity.ok(userService.search(clientId, pageable));
    }

}
