package com.webapp.example.message;

import jakarta.validation.constraints.NotNull;

public record MessageUpdateRequest(
    @NotNull
    long id, 
    @NotNull
    String contents
) {}
