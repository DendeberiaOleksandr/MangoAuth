package org.mango.auth.server.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiException extends RuntimeException {

    protected final Integer errorCode;
    protected final String code;
    protected final String message;
    protected final String logMessage;

    @Getter
    protected final Map<String, Object> data;

    protected ApiException(String message) {
        this.message = message;
        this.logMessage = message;
        this.data = new HashMap<>();
        code = null;
        errorCode = null;
    }

    protected ApiException(String message, String logMessage) {
        this.message = message;
        this.logMessage = logMessage;
        this.data = new HashMap<>();
        code = null;
        errorCode = null;
    }

    protected ApiException(String message, String logMessage, String code, int errorCode, Map<String, Object> data) {
        this.message = message;
        this.logMessage = logMessage;
        this.code = code;
        this.errorCode = errorCode;
        this.data = data;
    }

    public abstract String getCode();
    public abstract String getMessage();
    public abstract String getLogMessage();
    public abstract int getErrorCode();

}
