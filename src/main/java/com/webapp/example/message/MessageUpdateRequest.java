package com.webapp.example.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
    long id, 
    @NotNull
    @Size(min = 1, max = 1000)
    String contents
) {}
