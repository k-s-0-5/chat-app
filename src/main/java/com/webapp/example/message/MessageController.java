package com.webapp.example.message;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for messages, currently unused */
@RestController
@RequestMapping("/messages")
public class MessageController {

  @SuppressWarnings("unused")
  private final MessageRepository messageRepository;

  public MessageController(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }
}
