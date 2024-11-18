package org.mango.auth.server.exception;

import static org.mango.auth.server.util.ErrorCodes.SERVER_ERROR;

public class ServerException extends ApiException {
    public ServerException(String message) {
        super(message, message, SERVER_ERROR, 500, null);
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
