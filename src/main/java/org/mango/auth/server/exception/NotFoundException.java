package org.mango.auth.server.exception;

import static org.mango.auth.server.util.ErrorCodes.NOT_FOUND_ERROR;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return NOT_FOUND_ERROR;
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
        return 404;
    }
}
