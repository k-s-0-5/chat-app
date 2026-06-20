package com.webapp.example.account;

import java.util.UUID;

/** Account model */
public record Account(UUID id, String username, String email, String password, String role) {}
