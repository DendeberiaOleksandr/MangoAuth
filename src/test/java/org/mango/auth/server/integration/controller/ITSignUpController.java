package org.mango.auth.server.integration.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
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
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "test@example.com",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mvc.perform(post("/api/v1/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void signUp_whenInvalidEmail_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "invalid-email",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mvc.perform(post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_whenInvalidPassword_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "test@example.com",
                "password": "pa1",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mvc.perform(post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

}
