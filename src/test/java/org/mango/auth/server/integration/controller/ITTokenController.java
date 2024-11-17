package org.mango.auth.server.integration.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.integration.util.TestUtil;
import org.mango.auth.server.repository.RefreshTokenRepository;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.util.ErrorCodes.EXPIRED_REFRESH_TOKEN_ERROR;
import static org.mango.auth.server.util.ErrorCodes.INVALID_REFRESH_TOKEN_ERROR;
import static org.mango.auth.server.util.ErrorCodes.USER_IS_NOT_VERIFIED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    private String refreshToken;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        createUser("test@example.com", "password123", String.valueOf(CLIENT_ID_1));
        createUser("test@example.com", "password456", String.valueOf(TestUtil.CLIENT_ID_2));

        Map<String, String> tokens = createAndReturnTokens("test@example.com", "password123", CLIENT_ID_1);
        refreshToken = tokens.get("refreshToken");
        accessToken = tokens.get("accessToken");
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

    private Map<String, String> createAndReturnTokens(String email, String password, UUID clientId) {
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

            String accessToken = JsonPath.read(response, "$.accessToken.token");
            String refreshToken = JsonPath.read(response, "$.refreshToken.token");

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tokens", e);
        }
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

    @Test
    void refreshAccessToken_whenValidRefreshToken_thenReturnsNewAccessToken() throws Exception {
        Thread.sleep(5000);
        String oldAccessToken = accessToken;
        mvc.perform(post(ApiPaths.TOKEN_REFRESH)
                        .param("refreshToken", refreshToken)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").exists())
                .andDo(print())
                .andExpect(result -> {
                    String newAccessToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken.token");
                    assertNotEquals(oldAccessToken, newAccessToken);
                });
    }

    @Test
    void refreshAccessToken_whenInvalidRefreshToken_thenReturnsError() throws Exception {
        mvc.perform(post(ApiPaths.TOKEN_REFRESH)
                        .param("refreshToken", "invalidToken")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(INVALID_REFRESH_TOKEN_ERROR)))
                .andDo(print());
    }

    @Test
    void refreshAccessToken_whenExpiredRefreshToken_thenReturnsError() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Optional<RefreshToken> expiredTokenOpt = refreshTokenRepository.findByToken("expired_token_example");
        if (expiredTokenOpt.isEmpty()) {
            throw new RuntimeException("Test setup error: Expired refresh token not found in the database");
        }
        RefreshToken expiredToken = expiredTokenOpt.get();

        assertThat(expiredToken.getExpiryAt()).isBefore(now);

        mvc.perform(post(ApiPaths.TOKEN_REFRESH)
                        .param("refreshToken", expiredToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(EXPIRED_REFRESH_TOKEN_ERROR)))
                .andDo(print());
    }

    @Test
    void testSignOut_SuccessfulRevocation()throws Exception {
        mvc.perform(
                        delete(ApiPaths.TOKEN_SIGN_OUT)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + refreshToken)

                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testSignOut_InvalidToken() throws Exception {
        mvc.perform(
                        delete(ApiPaths.TOKEN_SIGN_OUT)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "invalidToken")
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}