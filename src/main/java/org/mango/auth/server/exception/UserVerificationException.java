package org.mango.auth.server.exception;

import lombok.Getter;

import java.util.Map;

public class UserVerificationException extends ApiException {

    @Getter
    private final long banTimeInSeconds;

    public UserVerificationException(String message, String logMessage, String code, Integer errorCode, long banTimeInSeconds) {
        super(message, logMessage, code, errorCode, Map.of("banTimeInSeconds", banTimeInSeconds));
        this.banTimeInSeconds = banTimeInSeconds;
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
        return message;
    }

    @Override
    public int getErrorCode() {
        return errorCode == null ? 500 : errorCode;
    }

}
