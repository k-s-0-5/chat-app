package com.webapp.example.message;

import com.webapp.example.Errors.MessageNotFoundException;
import com.webapp.example.account.AccountService;
import com.webapp.example.conversation.ConversationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final AccountService accountService;
  private final ConversationService conversationService;

  MessageService(
      MessageRepository messageRepository,
      AccountService accountService,
      ConversationService conversationService) {
    this.messageRepository = messageRepository;
    this.accountService = accountService;
    this.conversationService = conversationService;
  }

  public List<Message> findByConversationId(UUID conversationId) {
    return messageRepository.findByConversationId(conversationId);
  }

  public Message findById(long id) {
    return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
  }

  public Message createMessage(Message message, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    if (!accountId.equals(message.accountId())
        || !conversationService.isAccountInConversation(accountId, message.conversationId()))
      return null;
    Message fullMessage =
        new Message(
            -1,
            accountId,
            message.conversationId(),
            LocalDateTime.now(),
            message.contents(),
            false);
    return messageRepository.create(fullMessage);
  }

  public Message editMessage(Message message, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    Message originalMessage = findById(message.id());
    Message fullMessage =
        new Message(
            originalMessage.id(),
            originalMessage.accountId(),
            originalMessage.conversationId(),
            originalMessage.sentAt(),
            message.contents(),
            true);
    if (!accountId.equals(originalMessage.accountId())) return null;
    return messageRepository.update(fullMessage);
  }

  public UUID deleteMessage(Long id, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    Message message = findById(id);
    messageRepository.delete(id, accountId);
    return message.conversationId();
  }
}
