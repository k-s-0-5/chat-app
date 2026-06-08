package com.webapp.example.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record Conversation(UUID id, String title, LocalDateTime lastSent) {}
