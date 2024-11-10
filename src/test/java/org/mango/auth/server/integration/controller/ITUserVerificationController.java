package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.service.EmailService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID;
import static org.mango.auth.server.integration.util.TestUtil.USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.USER_ID;
import static org.mango.auth.server.util.ApiPaths.USER_API;
import static org.mango.auth.server.util.ErrorCodes.EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR;
import static org.mango.auth.server.util.ErrorCodes.INVALID_EMAIL_VERIFICATION_CODE_ERROR;
import static org.mango.auth.server.util.ErrorCodes.VERIFICATION_EMAIL_SEND_LIMIT_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ITUserVerificationController extends ITBase {

    @Autowired
    UserService userService;
    @Autowired
    UserClientRoleService userClientRoleService;
    @MockBean
    EmailService emailService;

    @Test
    void verify_whenUserExceedsCodeEnteringAttemptsAndResetTimeIsNotGone() throws Exception {
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusSeconds(10));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID, "code"
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
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusMinutes(500));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID, "code"
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
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCode("code");
        user.setEmailVerificationCodeEnteredTimes(1000);
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now().minusMinutes(500));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID, "code"
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
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCode("code");
        user.setEmailVerificationCodeEnteredTimes(1);
        userService.save(user);

        String json = objectMapper.writeValueAsString(new UserVerificationRequest(
                USER_EMAIL, CLIENT_ID, "code"
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
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCodeSentTimes(1000);
        user.setEmailVerificationCodeLastSentAt(LocalDateTime.now().minusSeconds(10));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID
        ));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code", is(VERIFICATION_EMAIL_SEND_LIMIT_ERROR)));
    }

    @Test
    void sendVerificationEmail_whenUserExceedsSendEmailAttemptsAndResetTimeIsGone() throws Exception {
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCodeSentTimes(1000);
        user.setEmailVerificationCodeLastSentAt(LocalDateTime.now().minusDays(31));
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID
        ));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, user.getEmailVerificationCodeSentTimes());
    }

    @Test
    void sendVerificationEmail_whenUserNotExceedSendEmailAttempts() throws Exception {
        User user = userService.getById(USER_ID);
        user.setEmailVerificationCodeSentTimes(1);
        userService.save(user);

        String json = objectMapper.writeValueAsString(new SendUserVerificationEmailRequest(
                USER_EMAIL, CLIENT_ID
        ));

        mvc.perform(
                        post(USER_API + "/send-verification-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isOk());

        assertEquals(2, user.getEmailVerificationCodeSentTimes());
    }

}
