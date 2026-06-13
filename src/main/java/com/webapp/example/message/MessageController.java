package com.webapp.example.message;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messages")
public class MessageController {

  private final MessageRepository messageRepository;

  public MessageController(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @GetMapping("/{id}")
  Message findById(@PathVariable long id) {
    Optional<Message> message = messageRepository.findById(id);
    if (message.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return message.get();
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  void create(@RequestBody Message message) {
    messageRepository.create(message);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/{id}")
  void update(@RequestBody Message message, @PathVariable long id) {
    messageRepository.update(message, id);
  }
}
