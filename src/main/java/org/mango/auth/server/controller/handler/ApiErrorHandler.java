package org.mango.auth.server.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.ApiErrorResponse;
import org.mango.auth.server.exception.ApiException;
import org.mango.auth.server.exception.ServerException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiErrorHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleException(ApiException e) {
        log.error(e.getLogMessage(), e);
        ApiErrorResponse response = new ApiErrorResponse(e);
        return ResponseEntity.status(e.getErrorCode()).body(response);
    }

    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        if ( !(e instanceof ApiException) ) {
            e = new ServerException(e.getMessage());
        }
        ResponseEntity<ApiErrorResponse> responseEntity = handleException((ApiException) e);
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }


}
