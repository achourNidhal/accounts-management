package com.socgen.accountsmanagement.domain.exceptions;

public class AccountNotFoundException extends RuntimeException {

    private static final String message = "Unknow operation type";

    public AccountNotFoundException() {
        super(message);
    }

}
