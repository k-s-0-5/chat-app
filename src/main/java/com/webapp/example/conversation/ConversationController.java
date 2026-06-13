package com.webapp.example.conversation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
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
@RequestMapping("/conversations")
public class ConversationController {

  private final ConversationRepository conversationRepository;

  public ConversationController(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @GetMapping("/{id}")
  Conversation findById(@PathVariable UUID id) {
    Optional<Conversation> conversation = conversationRepository.findById(id);
    if (conversation.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return conversation.get();
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  void create(@RequestBody Conversation conversation) {
    conversationRepository.create(conversation);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/{id}")
  void update(@RequestBody Conversation conversation, @PathVariable UUID id) {
    conversationRepository.update(conversation, id);
  }
}
