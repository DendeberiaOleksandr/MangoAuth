package org.mango.auth.server.integration.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;


@SpringBootTest
@AutoConfigureMockMvc
public class ITUserController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "test@example.com",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mockMvc.perform(post("/api/v1/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void whenInvalidEmail_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "invalid-email",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mockMvc.perform(post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidPassword_thenReturns400() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "test@example.com",
                "password": "pa1",
                "firstName": "John",
                "lastName": "Doe"
            }
        """;

        mockMvc.perform(post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

}
