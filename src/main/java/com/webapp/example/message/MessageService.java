package com.webapp.example.message;

import com.webapp.example.Errors.MessageNotFoundException;
import com.webapp.example.account.AccountService;
import com.webapp.example.conversation.ConversationService;

import java.time.LocalDate;
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

  public List<Message> findByConversationId(UUID conversationId, LocalDateTime timestamp) {
    return messageRepository.findByConversationId(conversationId, timestamp);
  }

  public Message findById(long id) {
    return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
  }

  public Message createMessage(MessageCreateRequest messageCreateRequest, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    if (!conversationService.isAccountInConversation(accountId, messageCreateRequest.conversationId())) return null;
    return messageRepository.create(messageCreateRequest, accountId);
  }

  public Message updateMessage(MessageUpdateRequest messageUpdateRequest, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    Message original = findById(messageUpdateRequest.id());
    if (!accountId.equals(original.accountId())) return null;
    return messageRepository.update(original, messageUpdateRequest.contents());
  }

  public UUID deleteMessage(Long id, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    Message message = findById(id);
    if (!accountId.equals(message.accountId())) return null;
    messageRepository.delete(id, accountId);
    return message.conversationId();
  }
}
