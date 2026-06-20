package com.webapp.example.message;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

  private final MessageRepository messageRepository;

  public MessageController(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }
}
