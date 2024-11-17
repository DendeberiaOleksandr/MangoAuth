package org.mango.auth.server.integration.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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
        createUser("test@example.com", "password123",  UserStatus.ACTIVE, Role.ADMIN);
        accessTokenForAdmin = createAndReturnAccessToken("test@example.com", "password123", CLIENT_ID_1);

        createUser("test1231@example.com", "password1123123",  UserStatus.ACTIVE, Role.USER);
        accessTokenForUser = createAndReturnAccessToken("test1231@example.com", "password1123123", CLIENT_ID_1);

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
        mvc.perform(get(CLIENT_API).param("email", USER_EMAIL)).andExpect(status().isOk()).andExpect(jsonPath("$", empty())).andDo(print());
    }

    @Test
    void createClient() throws Exception {
        String clientName = "Client New";

        CreateClientRequest request = new CreateClientRequest(clientName, USER_EMAIL);

        ResultActions result = mvc.perform(post(CLIENT_API).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isCreated()).andDo(print());

        Optional<UserClientRole> createdClient = userClientRoleService.findByUserEmailAndClientName(USER_EMAIL, clientName);
        assertTrue(createdClient.isPresent());
        Client client = createdClient.get().getClient();
        assertNotNull(client.getId());
    }

    @Test
    void getById() throws Exception {
        ResultActions result = mvc.perform(get(CLIENT_API + "/%s".formatted(CLIENT_ID_1.toString())).param("email", ADMIN_USER_EMAIL));

        result
        mvc.perform(
                        get(CLIENT_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessTokenForUser)
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

    private void createUser(String email, String password, UserStatus userStatus, Role role) {
        User user = User.builder()
                .email(email)
                .userStatus(userStatus)
                .password(passwordEncoder.encode(password))
                .build();
        userService.save(user);

        Client client = Client.builder()
                .id(CLIENT_ID_1)
                .name(CLIENT_NAME_1)
                .apiKey("testApiKey")
                .apiSecret("testApiSecret")
                .build();
        clientService.save(client);

        UserClientRole userClientRole = UserClientRole.builder()
                .user(user)
                .client(client)
                .role(role)
                .build();
        userClientRoleService.save(userClientRole);
    }

    private String createAndReturnAccessToken(String email, String password, UUID clientId) {
        String jsonRequest = String.format("""
        {
            "clientId": "%s",
            "email": "%s",
            "password": "%s"
        }
    """, clientId, email, password);

        try {
            String response = mvc.perform(post(ApiPaths.TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            return JsonPath.read(response, "$.accessToken.token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create access token", e);
        }
    }

}
