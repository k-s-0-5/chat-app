package com.webapp.example.conversation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for conversations, currently unused */
@RestController
@RequestMapping("/conversations")
public class ConversationController {

  @SuppressWarnings("unused")
  private final ConversationRepository conversationRepository;

  public ConversationController(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }
}
