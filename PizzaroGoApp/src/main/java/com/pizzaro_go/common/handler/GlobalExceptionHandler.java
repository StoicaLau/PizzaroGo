package com.pizzaro_go.common.handler;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that captures PGException thrown by any controller
 * and returns a standard response format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles custom PGException. These are usually business logic errors.
     * The original message is returned to the frontend.
     */
    @ExceptionHandler(PGException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handlePGException(PGException ex) {
        log.error("PGException caught: {}", ex.getMessage(), ex);
        return new MessageResponse(ex.getMessage());
    }
}
