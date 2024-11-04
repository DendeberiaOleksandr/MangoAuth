package org.mango.auth.server.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.SignUp.SignUpRequest;
import org.mango.auth.server.entity.User;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void map() {
        SignUpRequest signUpRequest = new SignUpRequest(UUID.randomUUID(), "email", "password", "name", "name");
        User user = userMapper.map(signUpRequest, PASSWORD_ENCODER);
        assertNotNull(user);
        assertEquals(signUpRequest.email(), user.getEmail());
        assertEquals(signUpRequest.firstName(), user.getFirstName());
        assertEquals(signUpRequest.lastName(), user.getLastName());
        assertTrue(PASSWORD_ENCODER.matches(signUpRequest.password(), user.getPassword()));
    }

}