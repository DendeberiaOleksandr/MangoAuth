package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.dto.key.SecretKey;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.mapper.ClientMapper;
import org.mango.auth.server.repository.ClientRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.SecretKeyService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserClientRoleService userClientRoleService;
    private final SecretKeyService secretKeyService;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    @Override
    public Client getById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client is not found by id: %s".formatted(id.toString())));
    }

    @Transactional
    @Override
    public CreateClientResponse create(CreateClientRequest request, UserDetailsImpl userDetails) {
        SecretKey secretKey = secretKeyService.generate();

        Client client = clientMapper.map(request, secretKey);
        clientRepository.save(client);

        UserClientRole usersClient = UserClientRole.builder().client(client).user(userDetails.getUser()).role(Role.OWNER).build();
        client.setUserRoles(List.of(usersClient));

        userClientRoleService.save(usersClient);

        return clientMapper.mapToResponse(client, secretKey);
    }

    @Transactional
    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }
}
