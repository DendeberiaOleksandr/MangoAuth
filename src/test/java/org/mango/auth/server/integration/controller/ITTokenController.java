package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.integration.util.TestUtil;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.util.ErrorCodes.USER_IS_NOT_VERIFIED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class ITTokenController extends ITBase {

    @Autowired
    private UserClientRoleService userClientRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;

    @BeforeEach
    public void setUp() {
        createUser("test@example.com", "password123", String.valueOf(CLIENT_ID_1));
        createUser("test@example.com", "password456", String.valueOf(TestUtil.CLIENT_ID_2));
    }

    private void createUser(String email, String password, String clientId) {
        createUser(email, password, clientId, UserStatus.ACTIVE);
    }

    private void createUser(String email, String password, String clientId, UserStatus userStatus) {
        User user = User.builder()
                .email(email)
                .userStatus(userStatus)
                .password(passwordEncoder.encode(password))
                .build();
        userService.save(user);

        Client client = clientService.getById(UUID.fromString(clientId));

        UserClientRole userClientRole = UserClientRole.builder()
                .user(user)
                .client(client)
                .role(Role.USER)
                .build();
        userClientRoleService.save(userClientRole);
    }

    @Test
    void generateToken_whenValidCredentials_thenReturnsToken() throws Exception {
        String jsonRequest = String.format("""
                    {
                        "clientId": "%s",
                        "email": "test@example.com",
                        "password": "password123"
                    }
                """, CLIENT_ID_1);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").exists())
                .andExpect(jsonPath("$.refreshToken.token").exists());
    }

    @Test
    void generateToken_whenUserIsUnverified_thenReturnError() throws Exception {
        final String email = "unverifiedUser@example.com";
        final String password = "password";
        createUser(email, password, CLIENT_ID_1.toString(), UserStatus.UNVERIFIED);

        String jsonRequest = String.format("""
                    {
                        "clientId": "%s",
                        "email": "%s",
                        "password": "%s"
                    }
                """, CLIENT_ID_1, email, password);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is(USER_IS_NOT_VERIFIED_ERROR)))
                .andDo(print());
    }

    @Test
    void generateToken_whenValidCredentialsForOtherUser_thenSignInFails() throws Exception {
        String jsonRequest = String.format("""
                    {
                        "clientId": "%s",
                        "email": "test@example.com",
                        "password": "password456"
                    }
                """, CLIENT_ID_1);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    void generateToken_whenValidCredentialsForSecondUser_thenSignInSucceeds() throws Exception {
        String jsonRequest = String.format("""
                    {
                        "clientId": "%s",
                        "email": "test@example.com",
                        "password": "password456"
                    }
                """, TestUtil.CLIENT_ID_2);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").exists())
                .andExpect(jsonPath("$.refreshToken.token").exists());
    }
}