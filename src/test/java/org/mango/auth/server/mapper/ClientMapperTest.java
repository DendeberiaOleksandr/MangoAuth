package org.mango.auth.server.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.CreateClientRequest;
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
        String apiKey = "apiKey";

        Client client = mapper.map(createClientRequest, apiKey);

        assertNotNull(client);
        assertEquals(createClientRequest.name(), client.getName());
        assertEquals(apiKey, client.getApiKey());
        assertNotNull(client.getCreatedAt());
    }

    @Test
    void mapToDetails() {
        Client client = Instancio.create(Client.class);

        ClientDto dto = mapper.map(client);

        assertNotNull(dto);
        assertEquals(client.getId(), dto.getId());
        assertEquals(client.getName(), dto.getName());
        assertEquals(client.getApiKey(), dto.getApiKey());
        assertEquals(client.getCreatedAt(), dto.getCreatedAt());
    }

}