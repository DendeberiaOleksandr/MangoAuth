package org.mango.auth.server.exception;

import static org.mango.auth.server.util.ErrorCodes.INVALID_TOKEN_ERROR;

public class InvalidTokenException extends ApiException {

    public InvalidTokenException(String message) {
        super(message, message, INVALID_TOKEN_ERROR, 401, null);
    }

    @Override
    public String getCode() {
        return code;
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
        return errorCode;
    }
}
