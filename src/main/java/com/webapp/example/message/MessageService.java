package com.webapp.example.message;

import com.webapp.example.account.AccountService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final AccountService accountService;
  private int id = 50;

  MessageService(MessageRepository messageRepository, AccountService accountService) {
    this.messageRepository = messageRepository;
    this.accountService = accountService;
  }

  public List<Message> findByConversationId(UUID conversationId) {
    return messageRepository.findByConversationId(conversationId);
  }

  public Message createMessage(Message message, String username) {
    Message fullMessage =
        new Message(
            id++,
            accountService.findByUsername(username).id(),
            message.conversationId(),
            LocalDateTime.now(),
            message.contents(),
            false,
            false);
    System.out.println(message.toString());
    messageRepository.create(fullMessage);
    return fullMessage;
  }
}
