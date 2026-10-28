package com.webapp.example.participant;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

  private final ParticipantRepository participantRepository;

  ParticipantService(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }

  /**
   * Retrieves all participants where participant.conversationId = conversationId
   *
   * @param conversationId
   * @return List of participants
   */
  public List<Participant> findAllInConversation(UUID conversationId) {
    return participantRepository.findAllInConversation(conversationId);
  }

  /**
   * @param accountId
   * @param conversationId
   * @return True if the account is in the conversation
   */
  public boolean isAccountInConversation(UUID accountId, UUID conversationId) {
    return participantRepository.isAccountInConversation(accountId, conversationId);
  }

  /**
   * Retrieves all participants where participant.conversationId = conversationId
   *
   * @param conversationId
   * @return List of participants
   */
  public List<Participant> findAllWithAccount(UUID accountId) {
    return participantRepository.findAllWithAccount(accountId);
  }

  /**
   * @param accountIds
   * @return true if a common conversation exists between all users
   */
  public boolean commonConversationExists(List<UUID> accountIds) {
    return participantRepository.commonConversationExists(accountIds);
  }
}
