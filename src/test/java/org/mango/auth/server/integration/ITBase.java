package org.mango.auth.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
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

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("flyway.url", postgres::getJdbcUrl);
        registry.add("flyway.user", postgres::getUsername);
        registry.add("flyway.password", postgres::getPassword);
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
