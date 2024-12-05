package org.mango.auth.server.integration.security;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.exception.ServerException;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.service.JwtService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.is;
import static org.mango.auth.server.security.JwtAuthenticationFilter.BEARER_PREFIX;
import static org.mango.auth.server.util.ApiPaths.CLIENT_API;
import static org.mango.auth.server.util.ErrorCodes.INVALID_CREDENTIALS_ERROR;
import static org.mango.auth.server.util.ErrorCodes.SERVER_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITJwtAuthenticationFilter extends ITBase {

    @MockBean
    JwtService jwtService;

    @Test
    void doFilterInternal_whenInvalidJWTToken() throws Exception {
        when(jwtService.validateToken(anyString()))
                .thenReturn(false);

        mvc.perform(
                post(CLIENT_API)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "some token")
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(INVALID_CREDENTIALS_ERROR)))
                .andExpect(jsonPath("$.message", is("Invalid JWT token")))
                .andDo(print());
    }

    @Test
    void doFilterInternal_whenServerError() throws Exception {
        String message = "Error happened";
        doThrow(new ServerException(message)).when(jwtService).validateToken(anyString());

        mvc.perform(
                        post(CLIENT_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "some token")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(SERVER_ERROR)))
                .andExpect(jsonPath("$.message", is(message)))
                .andDo(print());
    }

}
