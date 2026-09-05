package com.webapp.example.participant;

import java.time.LocalDateTime;
import java.util.UUID;

/** Participant model */
public record Participant(Integer id, UUID accountId, UUID conversationId, LocalDateTime lastRead, String role) {}
