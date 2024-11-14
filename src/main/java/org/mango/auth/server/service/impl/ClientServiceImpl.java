package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.mapper.ClientMapper;
import org.mango.auth.server.repository.ClientRepository;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.KeyGeneratorService;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserClientRoleService userClientRoleService;
    private final KeyGeneratorService keyGeneratorService;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    @Override
    public Client getById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client is not found by id: %s".formatted(id.toString())));
    }

    @Transactional
    @Override
    public Client create(CreateClientRequest request) {
        String userEmail = request.userEmail();

        String apiKey = keyGeneratorService.generate();

        Client client = clientMapper.map(request, apiKey);
        clientRepository.save(client);

        UserClientRole userClientRole = userClientRoleService.getByUserEmailAndMangoClient(userEmail);

        UserClientRole usersClient = UserClientRole.builder().client(client).user(userClientRole.getUser()).role(Role.OWNER).build();

        return userClientRoleService.save(usersClient).getClient();
    }
}
