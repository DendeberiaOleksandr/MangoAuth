package org.mango.auth.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.mango.auth.server.integration.util.TestUtil.ADMIN_USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.integration.util.TestUtil.USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.USER_PASSWORD;
import static org.mango.auth.server.security.JwtAuthenticationFilter.BEARER_PREFIX;
import static org.mango.auth.server.util.ApiPaths.CLIENT_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class ITBase {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected UserClientRoleService userClientRoleService;

    @Autowired
    protected MockMvc mvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.1"));

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("flyway.url", postgres::getJdbcUrl);
        registry.add("flyway.user", postgres::getUsername);
        registry.add("flyway.password", postgres::getPassword);
    }

    @SneakyThrows
    protected CreateClientResponse createClient() {
        createUser(ADMIN_USER_EMAIL, "password123",  UserStatus.ACTIVE, Role.ADMIN);
        String token = createAndReturnAccessToken(ADMIN_USER_EMAIL, "password123", CLIENT_ID_1);
        CreateClientRequest createClientRequest = new CreateClientRequest(Instancio.create(String.class));

        String jsonResponse = mvc.perform(
                post(CLIENT_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientRequest))
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, CreateClientResponse.class);
    }

    protected String signInUserRole() {
        createUser(USER_EMAIL, USER_PASSWORD, UserStatus.ACTIVE, Role.USER);
        return createAndReturnAccessToken(USER_EMAIL, USER_PASSWORD, CLIENT_ID_1);
    }

    protected String signIn() {
        createUser(USER_EMAIL, USER_PASSWORD, UserStatus.ACTIVE, Role.ADMIN);
        return createAndReturnAccessToken(USER_EMAIL, USER_PASSWORD, CLIENT_ID_1);
    }

    protected UserClientRole createUser(String email, String password, UserStatus userStatus, Role role) {
        User user = User.builder()
                .email(email)
                .userStatus(userStatus)
                .password(passwordEncoder.encode(password))
                .build();
        userService.save(user);

        Client client = clientService.getById(CLIENT_ID_1);

        UserClientRole userClientRole = UserClientRole.builder()
                .user(user)
                .client(client)
                .role(role)
                .build();
        return userClientRoleService.save(userClientRole);
    }

    protected String createAndReturnAccessToken(String email, String password, UUID clientId) {
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
