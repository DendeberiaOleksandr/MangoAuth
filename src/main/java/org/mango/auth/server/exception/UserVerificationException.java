package org.mango.auth.server.exception;

import lombok.Getter;

import java.util.Map;

public class UserVerificationException extends ApiException {

    private static final String BAN_TIME_IN_SECONDS = "banTimeInSeconds";

    @Getter
    private final long banTimeInSeconds;

    public UserVerificationException(String message, String logMessage, String code, Integer errorCode, long banTimeInSeconds) {
        super(message, logMessage, code, errorCode, Map.of(BAN_TIME_IN_SECONDS, banTimeInSeconds));
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
