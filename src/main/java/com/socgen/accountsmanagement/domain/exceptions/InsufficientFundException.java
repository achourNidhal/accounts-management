package com.socgen.accountsmanagement.domain.exceptions;

public class InsufficientFundException extends RuntimeException {

    private static final String message = "Insufficient balance";

    public InsufficientFundException() {
        super(message);
    }

}
