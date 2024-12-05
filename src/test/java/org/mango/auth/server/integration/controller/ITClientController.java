package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mango.auth.server.integration.util.TestUtil.ADMIN_USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_API_KEY_1;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_NAME_1;
import static org.mango.auth.server.integration.util.TestUtil.USER_EMAIL;
import static org.mango.auth.server.util.ApiPaths.CLIENT_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ITClientController extends ITBase {

    @Autowired
    private UserClientRoleService userClientRoleService;
    @Autowired
    private UserClientRoleRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;

    private static final String BEARER_PREFIX = "Bearer ";

    private String accessTokenForAdmin;
    private String accessTokenForUser;

    @BeforeEach
    public void setUp() {
        createUser(ADMIN_USER_EMAIL, "password123",  UserStatus.ACTIVE, Role.ADMIN);
        accessTokenForAdmin = createAndReturnAccessToken(ADMIN_USER_EMAIL, "password123", CLIENT_ID_1);

        createUser(USER_EMAIL, "password1123123",  UserStatus.ACTIVE, Role.USER);
        accessTokenForUser = createAndReturnAccessToken(USER_EMAIL, "password1123123", CLIENT_ID_1);
    }

    @Test
    void getUserClientsWhereIsAdminOrOwner() throws Exception {

        mvc.perform(
                get(CLIENT_API)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessTokenForAdmin)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(CLIENT_ID_1.toString())))
                .andExpect(jsonPath("$[0].name", is(CLIENT_NAME_1)))
                .andExpect(jsonPath("$[0].role", is(Role.ADMIN.name())))
                .andDo(print());
    }

    @Test
    void getUserClientsWhereIsAdminOrOwner_whenHasUserRoleOnly() throws Exception {
        mvc.perform(
                        get(CLIENT_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessTokenForUser)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()))
                .andDo(print());
    }


    @Test
    void createClient_whenValidRequest() throws Exception {
        String clientName = "Client New";

        CreateClientRequest request = new CreateClientRequest(clientName);

        ResultActions result = mvc
                .perform(
                        post(CLIENT_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessTokenForAdmin)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(clientName)))
                .andExpect(jsonPath("$.secretKey", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andDo(print());

        Optional<UserClientRole> createdClient = userClientRoleService.findByUserEmailAndClientName(ADMIN_USER_EMAIL, clientName);
        assertTrue(createdClient.isPresent());
        Client client = createdClient.get().getClient();
        assertNotNull(client.getId());
    }

    @Test
    void getById() throws Exception {
        mvc.perform(
                        get(CLIENT_API + "/%s".formatted(CLIENT_ID_1.toString()))
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessTokenForAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(CLIENT_ID_1.toString())))
                .andExpect(jsonPath("$.name", is(CLIENT_NAME_1)))
                .andExpect(jsonPath("$.apiKey", is(CLIENT_API_KEY_1)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andDo(print());
    }
    @Test
    void getById_whenUserDoesNotHaveAdminRoles() throws Exception {
        ResultActions result = mvc.perform(get(CLIENT_API + "/%s".formatted(CLIENT_ID_1.toString())).param("email", USER_EMAIL));

        result
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void getUserClientsWhereIsAdminOrOwner_withoutToken_shouldReturnUnauthorized() throws Exception {
        mvc.perform(
                        get(CLIENT_API)
                )
                .andExpect((status().isForbidden()))
                .andDo(print());
    }

}
