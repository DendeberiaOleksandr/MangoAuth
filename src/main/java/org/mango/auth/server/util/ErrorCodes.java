package org.mango.auth.server.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCodes {

    public static final String EMAIL_VERIFICATION_CODE_ENTER_LIMIT_ERROR = "emailVerificationCodeEnterLimit";
    public static final String VERIFICATION_EMAIL_SEND_LIMIT_ERROR = "verificationEmailSendLimit";
    public static final String INVALID_EMAIL_VERIFICATION_CODE_ERROR = "invalidEmailVerificationCode";
    public static final String USER_IS_NOT_VERIFIED_ERROR = "userIsNotVerified";
    public static final String INVALID_REFRESH_TOKEN_ERROR = "invalidRefreshToken";
    public static final String EXPIRED_REFRESH_TOKEN_ERROR = "expiredRefreshTokenError";
}
