package org.mango.auth.server.integration.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.integration.ITBase;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.security.JwtAuthenticationFilter.BEARER_PREFIX;
import static org.mango.auth.server.util.ApiPaths.USER_API;
import static org.mango.auth.server.util.ErrorCodes.INVALID_CREDENTIALS_ERROR;
import static org.mango.auth.server.util.ErrorCodes.INVALID_PARAMETER_ERROR;
import static org.mango.auth.server.util.ErrorCodes.NOT_FOUND_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ITUserController extends ITBase {

    @SneakyThrows
    @Test
    void searchUsers_whenServiceAccount() {
        CreateClientResponse clientResponse = createClient();

        mvc.perform(
                        get(USER_API)
                                .header(HttpHeaders.AUTHORIZATION, clientResponse.id() + ":" + clientResponse.apiKey())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void searchUsers_whenServiceAccountInvalidSecretProvided() {
        CreateClientResponse clientResponse = createClient();

        mvc.perform(
                        get(USER_API)
                                .header(HttpHeaders.AUTHORIZATION, clientResponse.id() + ":InvalidSecret")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(INVALID_CREDENTIALS_ERROR)))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void searchUsers_whenUserAccount() {
        String accessToken = signIn();

        mvc.perform(
                        get(USER_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
                                .param("clientId", CLIENT_ID_1.toString())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void searchUsers_whenUserAccountAndClientIdNotProvided() {
        String accessToken = signIn();

        mvc.perform(
                        get(USER_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(INVALID_PARAMETER_ERROR)))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void searchUsers_whenUserAccountAndUserRole() {
        String accessToken = signInUserRole();

        mvc.perform(
                        get(USER_API)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
                                .param("clientId", CLIENT_ID_1.toString())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(NOT_FOUND_ERROR)))
                .andExpect(jsonPath("$.message", is("Client 9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9 does not exist")))
                .andDo(print());
    }

}
