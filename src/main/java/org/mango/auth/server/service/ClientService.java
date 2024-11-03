package org.mango.auth.server.service;

import org.mango.auth.server.entity.Client;

import java.util.UUID;

public interface ClientService {

    Client getById(UUID id);

}
