package org.mango.auth.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Triple;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.token.TokenDto;
import org.mango.auth.server.dto.token.TokenResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.KeyService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    JwtServiceImpl jwtService;

    @Mock
    ClientService clientService;

    KeyService keyService = new KeyServiceImpl(new BCryptPasswordEncoder());

    JwtServiceImplTest() throws NoSuchAlgorithmException {}

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(
             clientService, objectMapper
        );
    }

    @Test
    void generateTokens() {
        // given

        // when
        Pair<TokenResponse, UserClientRole> tokenResponsePair = mockTokens();
        TokenResponse tokenResponse = tokenResponsePair.getFirst();

        // then
        assertNotNull(tokenResponse);
        TokenDto accessToken = tokenResponse.accessToken();
        TokenDto refreshToken = tokenResponse.refreshToken();
        assertNotNull(accessToken);
        assertTrue(StringUtils.hasText(accessToken.token()));

        assertNotNull(refreshToken);
        assertTrue(StringUtils.hasText(refreshToken.token()));
    }

    private Pair<TokenResponse, UserClientRole> mockTokens() {
        Pair<String, String> keysPair = keyService.generatePublicPrivateKeysPair();
        String privateKey = keysPair.getSecond();
        String publicKey = keysPair.getFirst();

        Client client = Instancio.of(Client.class).set(Select.field(Client::getPrivateKey), privateKey).set(Select.field(Client::getPublicKey), publicKey).create();
        User user = Instancio.create(User.class);
        Role role = Role.USER;

        TokenResponse tokenResponse = jwtService.generateTokens(user, client, role);

        return Pair.of(tokenResponse, UserClientRole.builder().client(client).user(user).role(role).build());
    }

    @Test
    void validateToken() {
        // given
        jwtService.setAccessTokenExpiration(3600000);

        Pair<TokenResponse, UserClientRole> tokenResponsePair = mockTokens();
        TokenResponse tokenResponse = tokenResponsePair.getFirst();

        Client client = tokenResponsePair.getSecond().getClient();

        when(clientService.getById(client.getId()))
                .thenReturn(client);

        // when
        boolean isValid = jwtService.validateToken(tokenResponse.accessToken().token());

        // then
        assertTrue(isValid);
    }

    @Test
    void getTokenPayload() {
        // given
        Pair<TokenResponse, UserClientRole> tokenResponsePair = mockTokens();
        TokenResponse tokenResponse = tokenResponsePair.getFirst();
        UserClientRole userClientRole = tokenResponsePair.getSecond();

        // when
        JwtServiceImpl.TokenPayload tokenPayload = jwtService.getTokenPayload(tokenResponse.accessToken().token());

        // then
        assertNotNull(tokenResponse);
        assertEquals(userClientRole.getUser().getEmail(), tokenPayload.getUserEmail());
        assertEquals(userClientRole.getClient().getId(), tokenPayload.getClientId());
        assertEquals(userClientRole.getRole().name(), tokenPayload.getRole());
    }

}