package com.webapp.example.config;

import com.webapp.example.account.AccountService;
import com.webapp.example.auth.JWTService;
import com.webapp.example.auth.MyUserDetailsService;
import com.webapp.example.conversation.ConversationService;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

// https://medium.com/@abasjama04/implementing-websockets-in-spring-boot-and-angular-for-real-time-bidirectional-communication-c3307d046dff
@Configuration
public class MessageInterceptor implements ChannelInterceptor {

  private final ConversationService conversationService;
  private final AccountService accountService;
  private final JWTService jwtService;
  private final MyUserDetailsService myUserDetailsService;

  MessageInterceptor(
      ConversationService conversationService,
      AccountService accountService,
      JWTService jwtService,
      MyUserDetailsService myUserDetailsService) {
    this.conversationService = conversationService;
    this.accountService = accountService;
    this.jwtService = jwtService;
    this.myUserDetailsService = myUserDetailsService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      throw new AccessDeniedException("Invalid STOMP message");
    }

    String destination = accessor.getDestination();

    // if (accessor.getCommand().equals(StompCommand.CONNECT)) { // CONNECT
    //   List<String> authHeaderList = accessor.getNativeHeader("Authorization");
    //   if (authHeaderList == null) {
    //     throw new AccessDeniedException("No authorization header provided");
    //   }
    //   String authHeader = authHeaderList.get(0);
    //   if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //     throw new AccessDeniedException("Invalid authorization");
    //   }

    //   String jwt = authHeader.substring(7);
    //   String username = jwtService.extractUsername(jwt);
    //   UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
    //   boolean valid = jwtService.validateToken(username, userDetails);
    //   if (valid) {
    //     UsernamePasswordAuthenticationToken authenticatedUser =
    //         new UsernamePasswordAuthenticationToken(
    //             userDetails, null, userDetails.getAuthorities());
    //     accessor.setUser(authenticatedUser);
    //   } else {
    //     throw new AccessDeniedException("Invalid authorization");
    //   }
    // } else 
      if (accessor.getCommand().equals(StompCommand.SUBSCRIBE)) { // SUBSCRIBE
      if (!destination.startsWith("/topic/messages.")) {
        throw new AccessDeniedException("Topic unavailable");
      }
      if (accessor.getUser() == null) {
        throw new AccessDeniedException("Subscriber not found");
      }
      String id = destination.substring(16);
      UUID uuid;
      try {
        uuid = UUID.fromString(id);
      } catch (IllegalArgumentException e) {
        throw new AccessDeniedException("Topic unavailable");
      }
      conversationService.findById(uuid);
      if (!conversationService.isAccountInConversation(
          accountService.findIdByUsername(accessor.getUser().getName()), uuid)) {
        throw new AccessDeniedException("Topic unavailable");
      }
    }

    return message;
  }
}
