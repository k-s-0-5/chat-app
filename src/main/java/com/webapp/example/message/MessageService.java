package com.webapp.example.message;

import com.webapp.example.Errors.MessageNotFoundException;
import com.webapp.example.account.AccountService;
import com.webapp.example.conversation.ConversationService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
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

  /**
   * Retrieves message where message.id == id else throws MessageNotFoundException
   *
   * @param id
   * @return Optional of message
   */
  public Message findById(long id) {
    return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
  }

  /**
   * Retrieves messages within a conversation sent before timestamp capped at 20 (excludes message with id)
   *
   * @param conversationId
   * @param timestamp
   * @param id
   * @return List of messages with a conversationId == conversationId
   */
  public List<Message> findByConversationId(UUID conversationId, LocalDateTime timestamp, Long id) {
    if (id != null) {
      return messageRepository.findByConversationId(conversationId, timestamp, id);
    } else {
      return messageRepository.findByConversationId(conversationId, LocalDateTime.now());
    }
  }

  /**
   * Adds a new message to the database
   *
   * @param messageCreateRequest
   * @param username
   * @return Created message
   */
  public Message create(MessageCreateRequest messageCreateRequest, String username) {
    if (messageCreateRequest.contents().getBytes(StandardCharsets.UTF_8).length > 1000) {
      throw new IllegalArgumentException("Message exceeds byte limit");
    }
    UUID accountId = accountService.findIdByUsername(username);
    if (!conversationService.isAccountInConversation(accountId, messageCreateRequest.conversationId())) {
      throw new AccessDeniedException("Not a participant in this conversation");
    }
    return messageRepository.create(messageCreateRequest, accountId);
  }

  /**
   * Updates message's contents with messageUpdateRequest's where message.id == messageUpdateRequest.id
   *
   * @param messageUpdateRequest
   * @param username
   * @return Updated message
   */
  public Message update(MessageUpdateRequest messageUpdateRequest, String username) {
    if (messageUpdateRequest.contents().getBytes(StandardCharsets.UTF_8).length > 1000) {
      throw new IllegalArgumentException("Message exceeds byte limit");
    }
    UUID accountId = accountService.findIdByUsername(username);
    Message original = findById(messageUpdateRequest.id());
    if (!accountId.equals(original.accountId())) {
      throw new AccessDeniedException("Not authorized to modify this message");
    }
    return messageRepository.update(original, messageUpdateRequest.contents());
  }

  /**
   * Deletes message where message.id = id
   *
   * @param id
   * @param username
   * @return ConversationId for broadcasting
   */
  public UUID delete(Long id, String username) {
    UUID accountId = accountService.findIdByUsername(username);
    Message message = findById(id);
    if (!accountId.equals(message.accountId())) {
      throw new AccessDeniedException("Not authorized to modify this message");
    }
    messageRepository.delete(id, accountId);
    return message.conversationId();
  }
}
