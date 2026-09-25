package com.webapp.example.Errors;

public class CryptoException extends RuntimeException {
    public CryptoException(String message){
        super(message);
    }
}
