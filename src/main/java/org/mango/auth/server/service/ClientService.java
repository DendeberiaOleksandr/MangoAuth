package org.mango.auth.server.service;

import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.security.UserDetailsImpl;

import java.util.UUID;

public interface ClientService {

    Client getById(UUID id);

    CreateClientResponse create(CreateClientRequest request, UserDetailsImpl userDetails);

    Client save(Client client);

}
