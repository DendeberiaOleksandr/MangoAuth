package org.mango.auth.server.validator;

import org.mango.auth.server.entity.User;
import org.mango.auth.server.exception.UserVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mango.auth.server.util.ErrorCodes.EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR;
import static org.mango.auth.server.util.ErrorCodes.VERIFICATION_EMAIL_SEND_LIMIT_ERROR;

@Component
public class UserVerificationValidator {

    private int emailVerificationCodeEnteredTimesLimit;
    private long emailVerificationCodeEnterResetTimeInSeconds;

    private int verificationEmailSentTimesLimit;
    private long verificationEmailSentResetTimeInSeconds;

    public void validate(User user, String code) {
        if (user.getEmailVerificationCodeEnteredTimes() > emailVerificationCodeEnteredTimesLimit) {

            long banTime = user.getEmailVerificationCodeLastEnteredAt().plusSeconds(emailVerificationCodeEnterResetTimeInSeconds)
                                    .toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            if (banTime < 0) {
                user.setEmailVerificationCodeEnteredTimes(0);
            } else {
                String message = "Email verification code entered more than %d times".formatted(emailVerificationCodeEnteredTimesLimit);
                throw new UserVerificationException(message, message, EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR, 429, banTime);
            }

        }
        user.setEmailVerificationCodeLastEnteredAt(LocalDateTime.now());
        user.setEmailVerificationCodeEnteredTimes(user.getEmailVerificationCodeEnteredTimes() + 1);
    }

    public void validateBeforeSendEmail(User user) {
        if (user.getEmailVerificationCodeSentTimes() > verificationEmailSentTimesLimit) {

            long banTime = user.getEmailVerificationCodeLastSentAt().plusSeconds(verificationEmailSentResetTimeInSeconds)
                    .toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            if (banTime < 0) {
                user.setEmailVerificationCodeSentTimes(0);
            } else {
                String message = "Verification email sent more than %d times".formatted(verificationEmailSentTimesLimit);
                throw new UserVerificationException(message, message, VERIFICATION_EMAIL_SEND_LIMIT_ERROR, 429, banTime);
            }

        }
        user.setEmailVerificationCodeLastSentAt(LocalDateTime.now());
        user.setEmailVerificationCodeSentTimes(user.getEmailVerificationCodeSentTimes() + 1);
    }

    @Value("${app.verification.emailVerificationCodeEnteredTimesLimit:3}")
    public void setEmailVerificationCodeEnteredTimesLimit(int emailVerificationCodeEnteredTimesLimit) {
        this.emailVerificationCodeEnteredTimesLimit = emailVerificationCodeEnteredTimesLimit;
    }

    @Value("${app.verification.emailVerificationCodeEnterResetTimeInSeconds:300}")
    public void setEmailVerificationCodeEnterResetTimeInSeconds(int emailVerificationCodeEnterResetTimeInSeconds) {
        this.emailVerificationCodeEnterResetTimeInSeconds = emailVerificationCodeEnterResetTimeInSeconds;
    }

    @Value("${app.verification.verificationEmailSentTimesLimit:3}")
    public void setVerificationEmailSentTimesLimit(int verificationEmailSentTimesLimit) {
        this.verificationEmailSentTimesLimit = verificationEmailSentTimesLimit;
    }

    @Value("${app.verification.verificationEmailSentResetTimeInSeconds:3600}")
    public void setVerificationEmailSentResetTimeInSeconds(long verificationEmailSentResetTimeInSeconds) {
        this.verificationEmailSentResetTimeInSeconds = verificationEmailSentResetTimeInSeconds;
    }
}
