package com.webapp.example.account;

public record Account(
        Integer id,
        String username,
        String email,
        String password) {
}
