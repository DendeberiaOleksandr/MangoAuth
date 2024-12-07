package org.mango.auth.server.exception;

import java.util.Map;

import static org.mango.auth.server.util.ErrorCodes.INVALID_PARAMETER_ERROR;

public class InvalidParameterException extends ApiException {
    public InvalidParameterException(String message) {
        super(message, message, INVALID_PARAMETER_ERROR, 400, Map.of());
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
