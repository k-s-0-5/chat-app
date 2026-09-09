package com.webapp.example.Errors;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(Long id){
        super("Message not found: " + id);
    }
}
