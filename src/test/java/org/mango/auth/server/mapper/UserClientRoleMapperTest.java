package org.mango.auth.server.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.entity.UserClientRole;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserClientRoleMapperTest {

    UserClientRoleMapper mapper = Mappers.getMapper(UserClientRoleMapper.class);

    @Test
    void map() {
        UserClientRole userClientRole = Instancio.create(UserClientRole.class);

        UserClientRoleLightDto result = mapper.map(userClientRole);

        assertNotNull(result);
        assertEquals(userClientRole.getClient().getId(), result.getClientId());
        assertEquals(userClientRole.getClient().getName(), result.getClientName());
        assertEquals(userClientRole.getRole(), result.getRole());
    }

}