package com.webapp.example.account;

import java.util.UUID;

public record Account(
        UUID id,
        String username,
        String email,
        String password) {
}
