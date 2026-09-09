package com.webapp.example.message;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageCreateRequest(
    UUID conversationId, 
    @NotNull
    @Size(min = 1, max = 1000)
    String contents
) {}
