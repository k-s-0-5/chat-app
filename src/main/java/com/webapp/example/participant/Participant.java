package com.webapp.example.participant;

import java.util.UUID;

/** Participant model */
public record Participant(Integer id, UUID accountId, UUID conversationId, String role) {}
