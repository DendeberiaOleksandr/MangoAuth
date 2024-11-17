package org.mango.auth.server.integration.controller;

import static javax.management.Query.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mango.auth.server.dto.EmailCallback;
import org.mango.auth.server.entity.EmailEventResult;
import org.junit.jupiter.api.AfterEach;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.service.EmailService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.util.ApiPaths;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Transactional
public class ITSignUpController extends ITBase {

    @Autowired
    private UserClientRoleService userClientRoleService;
    @Autowired
    private UserService userService;
    @MockBean
    private EmailService emailService;


    @Test
    void signUp_whenValidRequest_thenReturns200() throws Exception {
        final String email = "test@example.com";
        final String emailFrom = "from";
        final String emailSubject = "subject";
        String jsonRequest = """
                    {
                        "clientId": "%s",
                        "email": "%s",
                        "password": "password123",
                        "firstName": "John",
                        "lastName": "Doe"
                    }
                """.formatted(CLIENT_ID_1, email);


        EmailCallback emailCallback = new EmailCallback(emailFrom, emailSubject, EmailEventResult.SUCCEED);
        when(emailService.sendEmail(any()))
                .thenReturn(CompletableFuture.completedFuture(emailCallback));

        mvc.perform(post(ApiPaths.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        UserClientRole userClientRole = userClientRoleService.getByUserEmailAndClientId(email, CLIENT_ID_1);
        User user = userClientRole.getUser();
        assertEquals(UserStatus.UNVERIFIED, user.getUserStatus());
    }

    @Test
    void signUp_whenInvalidEmail_thenReturns400() throws Exception {
        String jsonRequest = """
                    {
                        "clientId": "%s",
                        "email": "invalid-email",
                        "password": "password123",
                        "firstName": "Aleksandr",
                        "lastName": "Dendeberia"
                    }
                """.formatted(CLIENT_ID_1);

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
                """.formatted(CLIENT_ID_1);

        mvc.perform(post(ApiPaths.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}