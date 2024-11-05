package org.mango.auth.server.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.repository.ClientRepository;
import org.mango.auth.server.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    @Override
    public Client getById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client is not found by id: %s".formatted(id.toString())));
    }
}
