package org.mango.auth.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.mango.auth.server.util.ApiPaths.CLIENT_API;

@RestController
@RequestMapping(CLIENT_API)
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final UserClientRoleService userClientRoleService;
    private final ClientService clientService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserClientRoleLightDto>> getUserClientsWhereIsAdminOrOwner(Authentication authentication) {
        UserDetailsImpl userDetails = ( (UserDetailsImpl) authentication.getPrincipal());
        return ResponseEntity.ok(userClientRoleService.getUserClientsWhereIsAdminOrOwner(userDetails));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getUserClient(Authentication authentication,
                                                   @PathVariable("id") UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userClientRoleService.getById(id, userDetails));
    }

    @PostMapping
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody @Valid CreateClientRequest request,
                                                             Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        CreateClientResponse response = clientService.create(request , userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
