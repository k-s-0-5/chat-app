package com.webapp.example.participant;

import java.util.UUID;

public record Participant(Integer id, UUID accountId, UUID conversationId, String role) {}
