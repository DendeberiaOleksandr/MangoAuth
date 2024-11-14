package org.mango.auth.server.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.ApiErrorResponse;
import org.mango.auth.server.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiErrorHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleException(ApiException e) {
        log.error(e.getLogMessage(), e);
        ApiErrorResponse response = new ApiErrorResponse(e);
        return ResponseEntity.status(e.getErrorCode()).body(response);
    }


}
