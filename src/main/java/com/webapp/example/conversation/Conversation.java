package com.webapp.example.conversation;

import java.time.LocalDateTime;

public record Conversation(
    Integer id,
    String title,
    LocalDateTime lastSent
){}
