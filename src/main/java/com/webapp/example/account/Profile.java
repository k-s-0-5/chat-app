package com.webapp.example.account;

import java.time.LocalDateTime;

/*
        Account DTO
**/
public record Profile(
        Integer id,
        String username,
        LocalDateTime created_at) {
}
