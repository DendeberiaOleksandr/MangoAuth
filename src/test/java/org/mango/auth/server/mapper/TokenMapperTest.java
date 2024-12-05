package org.mango.auth.server.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.token.IntrospectTokenResponse;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenMapperTest {

    TokenMapper mapper = Mappers.getMapper(TokenMapper.class);

    @Test
    void map() {
        UserDetailsImpl userDetails = Instancio.create(UserDetailsImpl.class);

        IntrospectTokenResponse introspectTokenResponse = mapper.map(userDetails);

        assertEquals(userDetails.getUser().getEmail(), introspectTokenResponse.email());
        assertEquals(userDetails.getUser().getCreatedAt(), introspectTokenResponse.registeredAt());
        assertEquals(userDetails.getRole(), introspectTokenResponse.role());
        assertEquals(userDetails.getClient().getId(), introspectTokenResponse.clientId());
        assertEquals(userDetails.getClient().getName(), introspectTokenResponse.clientName());
    }

}