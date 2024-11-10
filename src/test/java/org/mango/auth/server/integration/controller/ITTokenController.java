package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.integration.util.TestUtil;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class ITTokenController extends ITBase {

    @Autowired
    private UserClientRoleService userClientRoleService;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userClientRoleService.deleteAll();
        userService.deleteAll();

        createUser("test@example.com", "password123", String.valueOf(TestUtil.CLIENT_ID_1));
        createUser("test@example.com", "password456", String.valueOf(TestUtil.CLIENT_ID_2));
    }

    private void createUser(String email, String password, String clientId) {

        String signUpRequest = String.format("""
                {
                    "clientId": "%s",
                    "email": "%s",
                    "password": "%s",
                    "firstName": "John",
                    "lastName": "Doe"
                }
            """, clientId, email, password);

        try {
            mvc.perform(post(ApiPaths.SIGN_UP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(signUpRequest))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void whenValidCredentials_thenReturnsToken() throws Exception {
        String jsonRequest = String.format("""
            {
                "clientId": "%s",
                "email": "test@example.com",
                "password": "password123"
            }
        """, TestUtil.CLIENT_ID_1);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").exists())
                .andExpect(jsonPath("$.refreshToken.token").exists());
    }

    @Test
    void whenValidCredentialsForOtherUser_thenSignInFails() throws Exception {
        String jsonRequest = String.format("""
            {
                "clientId": "%s",
                "email": "test@example.com",
                "password": "password456"
            }
        """, TestUtil.CLIENT_ID_1);

        mvc.perform(post(ApiPaths.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                        .andExpect(status().isForbidden());
    }

    @Test
    void whenValidCredentialsForSecondUser_thenSignInSucceeds() throws Exception {
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