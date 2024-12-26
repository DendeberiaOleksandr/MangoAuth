package org.mango.auth.server.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.entity.Client;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

    ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void map() {
        CreateClientRequest createClientRequest = Instancio.create(CreateClientRequest.class);
        String rsaPublicKey = Instancio.create(String.class);
        String rsaPrivateKey = Instancio.create(String.class);
        String apiKey = Instancio.create(String.class);

        Client client = mapper.map(createClientRequest, rsaPublicKey, rsaPrivateKey, apiKey);

        assertNotNull(client);
        assertEquals(createClientRequest.name(), client.getName());
        assertNotNull(client.getCreatedAt());
        assertEquals(rsaPublicKey, client.getPublicKey());
        assertEquals(rsaPrivateKey, client.getPrivateKey());
    }

    @Test
    void mapToDetails() {
        Client client = Instancio.create(Client.class);

        ClientDto dto = mapper.map(client);

        assertNotNull(dto);
        assertEquals(client.getId(), dto.getId());
        assertEquals(client.getName(), dto.getName());
        assertEquals(client.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void mapToResponse() {
        Client client = Instancio.create(Client.class);
        String publicKey = Instancio.create(String.class);
        String apiKey = Instancio.create(String.class);

        CreateClientResponse response = mapper.mapToResponse(client, publicKey, apiKey);

        assertNotNull(response);
        assertEquals(client.getId(), response.id());
        assertEquals(client.getName(), response.name());
        assertEquals(client.getCreatedAt(), response.createdAt());
    }

}