package com.webapp.example.message;

import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
  private final MessageService messageService;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatWebSocketController(
      MessageService messageService, SimpMessagingTemplate messagingTemplate) {
    this.messageService = messageService;
    this.messagingTemplate = messagingTemplate;
  }

  //   Important note, SecurityContextHolder is a thread-local utility meaning it does not work
  // with WebSockets, principals do work however
  @MessageMapping("/messages.send")
  public void handleMessage(Message message, Principal principal) {
    System.out.println("Message from user: " + principal.getName());
    Message fullMessage = messageService.createMessage(message, principal.getName());
    messagingTemplate.convertAndSend("/topic/messages." + fullMessage.conversationId(), fullMessage);
  }
}
