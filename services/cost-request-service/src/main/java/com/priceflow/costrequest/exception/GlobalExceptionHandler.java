package com.priceflow.costrequest.exception;

import com.priceflow.costrequest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CostRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCostRequestNotFound(
            CostRequestNotFoundException exception) {

        return new ErrorResponse(
                "COST_REQUEST_NOT_FOUND",
                exception.getMessage(),
                Instant.now()
        );
    }

    @ExceptionHandler(InvalidCostRequestStatusException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidCostRequestStatus(
            InvalidCostRequestStatusException exception) {

        return new ErrorResponse(
                "INVALID_COST_REQUEST_STATUS",
                exception.getMessage(),
                Instant.now()
        );
    }
}