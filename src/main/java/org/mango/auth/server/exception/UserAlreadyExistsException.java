package org.mango.auth.server.exception;

public class UserAlreadyExistsException extends ApiException{

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "userAlreadyExists";
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
        return 409;
    }
}
