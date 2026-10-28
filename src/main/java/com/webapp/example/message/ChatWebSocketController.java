package com.webapp.example.message;

import com.webapp.example.account.AccountSearchResult;
import com.webapp.example.account.AccountService;
import com.webapp.example.participant.ParticipantService;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
  private final MessageService messageService;
  private final AccountService accountService;
  private final ParticipantService participantService;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatWebSocketController(
      MessageService messageService,
      AccountService accountService,
      ParticipantService participantService,
      SimpMessagingTemplate messagingTemplate) {
    this.messageService = messageService;
    this.accountService = accountService;
    this.messagingTemplate = messagingTemplate;
    this.participantService = participantService;
  }

  @MessageMapping("/messages.send")
  public void sendMessage(@Valid MessageCreateRequest messageCreateRequest, Principal principal) {
    UUID accountId = accountService.findIdByUsername(principal.getName());
    if (!participantService.isAccountInConversation(accountId, messageCreateRequest.conversationId())) {
      throw new AccessDeniedException("Not a participant in this conversation");
    }

    Message created = messageService.create(messageCreateRequest, accountId);
    messagingTemplate.convertAndSend(
        "/topic/messages." + created.conversationId(), Map.of("event", "send", "body", created));
    List<AccountSearchResult> accounts =
        accountService.getAccountsInConversations(participantService.findAllInConversation(created.conversationId()));
    for (int i = 0; i < accounts.size(); i++) {
      messagingTemplate.convertAndSend(
          "/topic/" + accounts.get(i).id(), Map.of("body", created.conversationId()));
    }
  }

  @MessageMapping("/messages.edit")
  public void editMessage(@Valid MessageUpdateRequest messageUpdateRequest, Principal principal) {
    Message edited = messageService.update(messageUpdateRequest, principal.getName());
    messagingTemplate.convertAndSend(
        "/topic/messages." + edited.conversationId(), Map.of("event", "edit", "body", edited));
  }

  @MessageMapping("/messages.delete")
  public void deleteMessage(Long id, Principal principal) {
    UUID conversationId = messageService.delete(id, principal.getName());
    messagingTemplate.convertAndSend(
        "/topic/messages." + conversationId, Map.of("event", "delete", "body", id));
  }
}
