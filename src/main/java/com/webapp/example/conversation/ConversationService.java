package com.webapp.example.conversation;

import com.webapp.example.account.Account;
import com.webapp.example.participant.Participant;
import com.webapp.example.participant.ParticipantRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
  private final ConversationRepository conversationRepository;
  private final ParticipantRepository participantRepository;

  ConversationService(
      ConversationRepository conversationRepository, ParticipantRepository participantRepository) {
    this.conversationRepository = conversationRepository;
    this.participantRepository = participantRepository;
  }

  public List<Conversation> getMyConversations(Account account) {
    List<Participant> participants = participantRepository.findAllWithAccount(account);
    List<Conversation> conversations = new ArrayList<Conversation>();
    for (Participant p : participants) {
      conversations.add(conversationRepository.findById(p.conversationId()).get());
    }
    return conversations;
  }
}
