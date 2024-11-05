package org.mango.auth.server.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiException extends RuntimeException {

    protected final String message;
    protected final String logMessage;

    @Getter
    protected final Map<String, String> data;

    protected ApiException(String message) {
        this.message = message;
        this.logMessage = message;
        this.data = new HashMap<>();
    }

    protected ApiException(String message, String logMessage) {
        this.message = message;
        this.logMessage = logMessage;
        this.data = new HashMap<>();
    }

    protected ApiException(String message, String logMessage, Map<String, String> data) {
        this.message = message;
        this.logMessage = logMessage;
        this.data = data;
    }

    public abstract String getCode();
    public abstract String getMessage();
    public abstract String getLogMessage();
    public abstract int getErrorCode();

}
