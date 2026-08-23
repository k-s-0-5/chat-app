package com.webapp.example.Errors;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID id){
        super("Account not found: " + id);
    }

    public AccountNotFoundException(String name){
        super("Account not found: " + name);
    }
}
