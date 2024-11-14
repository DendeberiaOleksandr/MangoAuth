package org.mango.auth.server.exception;

import java.util.HashMap;

import static org.mango.auth.server.util.ErrorCodes.INVALID_REFRESH_TOKEN_ERROR;


public class InvalidRefreshTokenException extends ApiException {

    public static final int ERROR_CODE = 403;

    public InvalidRefreshTokenException(String message) {
        super(message, message, INVALID_REFRESH_TOKEN_ERROR, ERROR_CODE, new HashMap<>());
    }

    @Override
    public String getCode() {
        return INVALID_REFRESH_TOKEN_ERROR;
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