package com.priceflow.costrequest.exception;

public class CostRequestNotFoundException extends RuntimeException {

    public CostRequestNotFoundException(String requestId) {
        super("Cost request not found: " + requestId);
    }
}