package com.webapp.example.message;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private final MessageRepository messageRepository;

  MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public List<Message> findByConversationId(UUID conversationId) {
    return messageRepository.findByConversationId(conversationId);
  }

  public void createMessage(Message message) {
    messageRepository.create(message);
  }
}
