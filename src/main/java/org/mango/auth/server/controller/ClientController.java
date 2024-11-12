package org.mango.auth.server.controller;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.mango.auth.server.util.ApiPaths.CLIENT_API;

@RestController
@RequestMapping(CLIENT_API)
@RequiredArgsConstructor
public class ClientController {

    private final UserClientRoleService userClientRoleService;

    @GetMapping
    public ResponseEntity<List<UserClientRoleLightDto>> getUserClientsWhereIsAdminOrOwner(@RequestParam("email") String email) {
        return ResponseEntity.ok(userClientRoleService.getUserClientsWhereIsAdminOrOwner(email));
    }

}
