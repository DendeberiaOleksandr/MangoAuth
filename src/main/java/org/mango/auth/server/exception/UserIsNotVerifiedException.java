package org.mango.auth.server.exception;

import java.util.Map;

import static org.mango.auth.server.util.ErrorCodes.USER_IS_NOT_VERIFIED_ERROR;

public class UserIsNotVerifiedException extends ApiException{

    public UserIsNotVerifiedException(String message) {
        super(message, message, USER_IS_NOT_VERIFIED_ERROR, 409, Map.of());
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
