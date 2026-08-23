package com.webapp.example.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotNull
    @Size(min = 4, max = 25, message = "Username must be between 3 and 25 characters")
    String username, 
    
    @NotNull
    @Size(min = 3, message = "Email must be at least 3 characters")
    @Email(message = "Email must be valid")
    String email, 

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password) {}
