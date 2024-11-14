package org.mango.auth.server.service;

import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.entity.Client;

import java.util.UUID;

public interface ClientService {

    Client getById(UUID id);
    Client create(CreateClientRequest request);

}
