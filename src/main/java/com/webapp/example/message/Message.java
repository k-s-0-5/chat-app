package com.webapp.example.message;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;

public record Message(
    Integer id, 
    Integer accountId,
    Integer conversationId, 
    LocalDateTime sentAt, 
    Type contentType,
    String contents,
    @Nullable
    String attachmentUrl, 
    String tag, 
    boolean isRead, 
    boolean edited
) {} 