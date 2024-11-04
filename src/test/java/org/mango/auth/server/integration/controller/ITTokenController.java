package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ITTokenController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        String jsonRequest = """
            {
                "clientId": "9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9",
                "email": "test@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/api/v1/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }


}
