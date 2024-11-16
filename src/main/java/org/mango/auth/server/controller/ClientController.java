package org.mango.auth.server.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<List<UserClientRoleLightDto>> getUserClientsWhereIsAdminOrOwner(@RequestParam("email") String email) {
        return ResponseEntity.ok(userClientRoleService.getUserClientsWhereIsAdminOrOwner(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getUserClient(@PathVariable("id") UUID id,
            @RequestParam("email") String email) {
        return ResponseEntity.ok(userClientRoleService.getById(id, email));
    }

    @PostMapping
    public ResponseEntity<Void> createClient(@RequestBody @Valid CreateClientRequest request) {
        clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
