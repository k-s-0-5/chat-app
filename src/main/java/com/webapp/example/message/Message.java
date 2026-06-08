package com.webapp.example.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(
    long id, 
    UUID accountId,
    Integer conversationId, 
    LocalDateTime sentAt, 
    Type contentType,
    String contents,
    String attachmentUrl, 
    boolean isRead, 
    boolean edited
) {} 