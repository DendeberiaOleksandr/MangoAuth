package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.EmailProperties;
import org.mango.auth.server.dto.verification.SendUserVerificationEmailRequest;
import org.mango.auth.server.dto.verification.UserVerificationRequest;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.UserStatus;
import org.mango.auth.server.exception.UserVerificationException;
import org.mango.auth.server.service.CodeGenerationService;
import org.mango.auth.server.service.EmailService;
import org.mango.auth.server.service.MailGeneratorService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.mango.auth.server.service.UserVerificationService;
import org.mango.auth.server.validator.UserVerificationValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.mango.auth.server.util.ErrorCodes.INVALID_EMAIL_VERIFICATION_CODE_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerificationServiceImpl implements UserVerificationService {

    private static final String VERIFICATION_EMAIL_SUBJECT = "Verify Your Account";

    private final UserClientRoleService userClientRoleService;
    private final UserVerificationValidator userVerificationValidator;
    private final UserService userService;
    private final CodeGenerationService codeGenerationService;
    private final EmailService emailService;
    private final MailGeneratorService mailGeneratorService;

    @Transactional(noRollbackFor = UserVerificationException.class)
    @Override
    public void verify(UserVerificationRequest request) {
        UserClientRole userClientRole = userClientRoleService.getByUserEmailAndClientId(request.getEmail(), request.getClientId());

        User user = userClientRole.getUser();

        userVerificationValidator.validate(user, request.getCode());

        if (request.getCode().equals(user.getEmailVerificationCode())) {
            user.setEmailVerificationCodeEnteredTimes(0);
            user.setEmailVerificationCode(null);
            user.setUserStatus(UserStatus.ACTIVE);
            userService.save(user);
        } else {
            userService.save(user);
            final String message = "Invalid email verification code provided";
            throw new UserVerificationException(message, message, INVALID_EMAIL_VERIFICATION_CODE_ERROR, 409, 0);
        }
    }

    @Transactional
    @Override
    public void sendVerificationEmail(SendUserVerificationEmailRequest request) {
        UserClientRole userClientRole = userClientRoleService.getByUserEmailAndClientId(request.getEmail(), request.getClientId());
        sendVerificationEmail(userClientRole);
    }

    @Transactional
    @Override
    public void sendVerificationEmail(UserClientRole userClientRole) {
        User user = userClientRole.getUser();

        userVerificationValidator.validateBeforeSendEmail(user);
        userService.save(user);

        String verificationCode = codeGenerationService.generate();

        String email = user.getEmail();
        String html = mailGeneratorService.generateActivationEmail(email, verificationCode, userClientRole.getClient().getName());
        emailService.sendEmail(new EmailProperties(VERIFICATION_EMAIL_SUBJECT, email, html));
    }
}
