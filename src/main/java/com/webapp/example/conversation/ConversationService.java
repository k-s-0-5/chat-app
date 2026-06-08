package com.webapp.example.conversation;

import com.webapp.example.account.Account;
import com.webapp.example.participant.Participant;
import com.webapp.example.participant.ParticipantController;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
  private final ConversationController conversationController;
  private final ParticipantController participantController;

  ConversationService(
      ConversationController conversationController, ParticipantController participantController) {
    this.conversationController = conversationController;
    this.participantController = participantController;
  }

  public List<Conversation> getMyConversations(Account account) {
    List<Participant> participants = participantController.findAll(account);
    List<Conversation> conversations = new ArrayList<Conversation>();
    for (Participant p : participants) {
      conversations.add(conversationController.findById(p.conversationId()));
    }
    return conversations;
  }
}
