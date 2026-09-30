package com.webapp.example.message;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record MessageCreateRequest(
    @NotNull
    UUID conversationId, 
    @NotNull
    String contents
) {}
