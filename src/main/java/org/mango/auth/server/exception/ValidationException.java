package org.mango.auth.server.exception;

import org.mango.auth.server.util.ErrorCodes;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message, message, ErrorCodes.VALIDATION_ERROR, 400, null);
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
