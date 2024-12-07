package org.mango.auth.server.exception;

import java.util.Map;

import static org.mango.auth.server.util.ErrorCodes.UNSUPPORTED_ERROR;

public class UnsupportedException extends ApiException {

    public UnsupportedException(String message) {
        super(message, message, UNSUPPORTED_ERROR, 405, Map.of());
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
