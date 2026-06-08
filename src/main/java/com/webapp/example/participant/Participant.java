package com.webapp.example.participant;

public record Participant(
        Integer id,
        Integer accountId,
        Integer conversationId,
        String role
) {}
