package com.webapp.example.config;

import com.webapp.example.account.AccountService;
import com.webapp.example.conversation.ConversationService;
import com.webapp.example.participant.ParticipantService;
import java.util.UUID;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;

// https://medium.com/@abasjama04/implementing-websockets-in-spring-boot-and-angular-for-real-time-bidirectional-communication-c3307d046dff
@Configuration
public class MessageInterceptor implements ChannelInterceptor {

  private final ParticipantService participantService;
  private final ConversationService conversationService;
  private final AccountService accountService;

  MessageInterceptor(
      ConversationService conversationService,
      AccountService accountService,
      ParticipantService participantService) {
    this.conversationService = conversationService;
    this.accountService = accountService;
    this.participantService = participantService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) {
      throw new AccessDeniedException("Invalid STOMP message");
    }
    String destination = accessor.getDestination();

    if (accessor.getCommand().equals(StompCommand.SUBSCRIBE)) {
      if (accessor.getUser() == null) {
        throw new AccessDeniedException("Subscriber not found");
      }
      UUID uuid;
      if (destination.startsWith("/topic/messages.")) {
        String id = destination.substring(16);
        try {
          uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
          throw new AccessDeniedException("Topic unavailable");
        }
        if (!participantService.isAccountInConversation(
            accountService.findIdByUsername(accessor.getUser().getName()), uuid)) {
          throw new AccessDeniedException("Topic unavailable");
        }
      } else {
        String id = destination.substring(7);
        try {
          uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
          throw new AccessDeniedException("Topic unavailable");
        }
        if (!uuid.equals(accountService.findByUsername(accessor.getUser().getName()).id())) {
          throw new AccessDeniedException("Topic unavailable");
        }
      }
    }

    return message;
  }
}
