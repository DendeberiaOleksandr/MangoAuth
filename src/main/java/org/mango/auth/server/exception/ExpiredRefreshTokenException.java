package org.mango.auth.server.exception;

import java.util.HashMap;

import static org.mango.auth.server.util.ErrorCodes.EXPIRED_REFRESH_TOKEN_ERROR;

public class ExpiredRefreshTokenException extends ApiException {

    public static final int ERROR_CODE = 403;

    public ExpiredRefreshTokenException(String message) {
        super(message, message, EXPIRED_REFRESH_TOKEN_ERROR, ERROR_CODE, new HashMap<>());
    }

    @Override
    public String getCode() {
        return EXPIRED_REFRESH_TOKEN_ERROR;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLogMessage() {
        return logMessage;
    }

    @Override
    public int getErrorCode() {
        return ERROR_CODE;
    }
}
