package org.mango.auth.server.mapper;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RefreshTokenMapperTest {

    private final RefreshTokenMapper mapper = Mappers.getMapper(RefreshTokenMapper.class);

    @Test
    void map(){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("TestClient");

        String deviceAgent = "Mozilla/5.0";
        String token = "token123";
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiryAt = issuedAt.plusDays(1);

        RefreshToken refreshToken = mapper.map(user, client, deviceAgent, token, issuedAt, expiryAt);

        assertNull(refreshToken.getId());
        assertEquals(user, refreshToken.getUser());
        assertEquals(client, refreshToken.getClient());
        assertEquals(deviceAgent, refreshToken.getDeviceAgent());
        assertEquals(token, refreshToken.getToken());
        assertEquals(issuedAt, refreshToken.getIssuedAt());
        assertEquals(expiryAt, refreshToken.getExpiryAt());
    }
}
