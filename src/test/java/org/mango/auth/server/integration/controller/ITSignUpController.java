package org.mango.auth.server.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.integration.util.TestUtil;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

public class ITSignUpController extends ITBase {

    @Autowired
    private UserClientRoleService userClientRoleService;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp(){
        userClientRoleService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void signUp_whenValidRequest_thenReturns200() throws Exception {
        String jsonRequest = """
        {
            "clientId": "%s",
            "email": "test@example.com",
            "password": "password123",
            "firstName": "John",
            "lastName": "Doe"
        }
    """.formatted(TestUtil.CLIENT_ID_1);

        mvc.perform(post(ApiPaths.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void signUp_whenInvalidEmail_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "%s",
                "email": "invalid-email",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe"
            }
        """.formatted(TestUtil.CLIENT_ID_1);

        mvc.perform(post(ApiPaths.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_whenInvalidPassword_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "%s",
                "email": "test@example.com",
                "password": "pa1",
                "firstName": "John",
                "lastName": "Doe"
            }
        """.formatted(TestUtil.CLIENT_ID_1);

        mvc.perform(post(ApiPaths.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
