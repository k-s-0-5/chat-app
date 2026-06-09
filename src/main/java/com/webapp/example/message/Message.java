package com.webapp.example.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(
    long id,
    UUID accountId,
    UUID conversationId,
    LocalDateTime sentAt,
    String contents,
    boolean isRead,
    boolean edited) {}
