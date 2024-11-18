package org.mango.auth.server.integration.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.dto.EmailCallback;
import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;
import org.mango.auth.server.entity.EmailAudit;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.EmailEvent;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.repository.EmailAuditRepository;
import org.mango.auth.server.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.integration.util.TestUtil.USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.USER_PASSWORD;
import static org.mango.auth.server.util.ApiPaths.USER_API;
import static org.mango.auth.server.util.ErrorCodes.EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR;
import static org.mango.auth.server.util.ErrorCodes.INVALID_EMAIL_VERIFICATION_CODE_ERROR;
import static org.mango.auth.server.util.ErrorCodes.VERIFICATION_EMAIL_SEND_LIMIT_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ITUserVerificationController extends ITBase {

    @MockBean
    EmailService emailService;
    @Autowired
    EmailAuditRepository emailAuditRepository;

    @Test
    void verify_whenUserExceedsCodeEnteringAttemptsAndResetTimeIsNotGone() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCode("fdfdfd");
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusSeconds(10));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID_1, "code"
        ));

        mvc.perform(
                        post(USER_API + "/verify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code", is(EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR)));

        assertNotNull(user.getEmailVerificationCode());
        assertEquals(UserStatus.UNVERIFIED, user.getUserStatus());
    }

    @Test
    void verify_whenUserExceedsCodeEnteringAttemptsAndResetTimeIsGoneAndInvalidCodeEntered() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCode("dsdsds");
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusMinutes(500));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID_1, "code"
        ));

        mvc.perform(
                        post(USER_API + "/verify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.code", is(INVALID_EMAIL_VERIFICATION_CODE_ERROR)));

        assertEquals(1, user.getEmailVerificationCodeEnteredTimes());
        assertNotNull(user.getEmailVerificationCode());
        assertEquals(UserStatus.UNVERIFIED, user.getUserStatus());
    }

    @Test
    void verify_whenUserExceedsCodeEnteringAttemptsAndResetTimeIsGoneAndValidCodeEntered() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCode("code");
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusMinutes(500));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID_1, "code"
        ));

        mvc.perform(
                        post(USER_API + "/verify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(200));

        assertNull(user.getEmailVerificationCode());
        assertEquals(0, user.getEmailVerificationCodeEnteredTimes());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void verify_whenUserNotExceedCodeEnteringAttemptsAndValidCodeEntered() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCode("code");
        user.setEmailVerificationCodeEnteredTimes(1);
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID_1, "code"
        ));

        mvc.perform(
                        post(USER_API + "/verify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(200));

        assertNull(user.getEmailVerificationCode());
        assertEquals(0, user.getEmailVerificationCodeEnteredTimes());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void sendVerificationEmail_whenUserExceedsSendEmailAttemptsAndResetTimeIsNotGone() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCodeSentTimes(1000);
        user.setEmailVerificationCodeLastSentAt(LocalDateTime.now().minusSeconds(10));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID_1
        ));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code", is(VERIFICATION_EMAIL_SEND_LIMIT_ERROR)));
        assertEmailAuditNotSaved();
    }

    @Test
    void sendVerificationEmail_whenUserExceedsSendEmailAttemptsAndResetTimeIsGone() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCodeSentTimes(1000);
        user.setEmailVerificationCodeLastSentAt(LocalDateTime.now().minusDays(31));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID_1
        ));

        EmailCallback emailCallback = Instancio.create(EmailCallback.class);
        when(emailService.sendEmail(any()))
                .thenReturn(CompletableFuture.completedFuture(emailCallback));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, user.getEmailVerificationCodeSentTimes());
        assertEmailAuditSaved(emailCallback);
    }

    @Test
    void sendVerificationEmail_whenUserNotExceedSendEmailAttempts() throws Exception {
        UserClientRole userClientRole = createUser(USER_EMAIL, USER_PASSWORD, UserStatus.UNVERIFIED, Role.USER);

        User user = userClientRole.getUser();
        user.setEmailVerificationCodeSentTimes(1);
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID_1
        ));

        EmailCallback emailCallback = Instancio.create(EmailCallback.class);
        when(emailService.sendEmail(any()))
                .thenReturn(CompletableFuture.completedFuture(emailCallback));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isOk());

        assertEquals(2, user.getEmailVerificationCodeSentTimes());
        assertEmailAuditSaved(emailCallback);
    }

    private void assertEmailAuditNotSaved() {
        List<EmailAudit> emailAudits = emailAuditRepository.findAll();
        assertTrue(emailAudits.isEmpty());
    }

    private void assertEmailAuditSaved(EmailCallback emailCallback) {
        List<EmailAudit> emailAudits = emailAuditRepository.findAll();
        assertEquals(1, emailAudits.size());
        EmailAudit emailAudit = emailAudits.get(0);
        assertNotNull(emailAudit);
        assertEquals(emailCallback.emailFrom(), emailAudit.getEmailFrom());
        assertEquals(emailCallback.subject(), emailAudit.getEmailSubject());
        assertEquals(EmailEvent.ACCOUNT_VERIFICATION, emailAudit.getEmailEvent());
        assertEquals(emailCallback.emailEventResult(), emailAudit.getEmailEventResult());
        assertNotNull(emailAudit.getUser());
        assertNotNull(emailAudit.getClient());
        assertNotNull(emailAudit.getId());
        assertNotNull(emailAudit.getSentAt());
    }

}
