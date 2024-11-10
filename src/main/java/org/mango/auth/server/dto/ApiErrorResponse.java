package org.mango.auth.server.dto;

import lombok.Getter;
import org.mango.auth.server.exception.ApiException;

import java.util.Map;

@Getter
public class ApiErrorResponse {
    private final String message;
    private final String code;
    private final Map<String, Object> data;

    public ApiErrorResponse(ApiException e) {
        this.message = e.getMessage();
        this.code = e.getCode();
        this.data = e.getData();
    }

}
