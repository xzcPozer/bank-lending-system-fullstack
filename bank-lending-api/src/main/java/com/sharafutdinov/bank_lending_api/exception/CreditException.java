package com.sharafutdinov.bank_lending_api.exception;

public class CreditException extends RuntimeException{
    public CreditException(String message) {
        super(message);
    }
}
