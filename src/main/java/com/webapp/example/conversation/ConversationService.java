package com.webapp.example.conversation;

import com.webapp.example.Errors.ConversationNotFoundException;
import com.webapp.example.account.AccountService;
import com.webapp.example.participant.Participant;
import com.webapp.example.participant.ParticipantRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
  private final AccountService accountService;
  private final ConversationRepository conversationRepository;
  private final ParticipantRepository participantRepository;

  ConversationService(
      ConversationRepository conversationRepository,
      ParticipantRepository participantRepository,
      AccountService accountService) {
    this.conversationRepository = conversationRepository;
    this.participantRepository = participantRepository;
    this.accountService = accountService;
  }

  /**
   * Retrieves conversation where conversation.id == id
   *
   * @param id
   * @return Conversation
   */
  public Conversation findById(UUID id) {
    return conversationRepository
        .findById(id)
        .orElseThrow(() -> new ConversationNotFoundException(id));
  }

  /**
   * @param accountid
   * @param conversationId
   * @return True if the account is in the conversation
   */
  public boolean isAccountInConversation(UUID accountid, UUID conversationId) {
    return participantRepository.isAccountInConversation(accountid, conversationId);
  }

  /**
   * Retrieves a list of conversations where participant.accountId == accountId
   *
   * @param accountId
   * @return List of conversations
   */
  public List<Conversation> getMyConversations(UUID accountId) {
    List<Participant> participants = participantRepository.findAllWithAccount(accountId);
    List<Conversation> conversations = new ArrayList<Conversation>();
    for (Participant p : participants) {
      conversations.add(findById(p.conversationId()));
    }
    sortByTime(conversations);
    return conversations;
  }

  /**
   * Creates conversation, returns early if conversation with all accounts already exists
   *
   * @param accountIds
   * @return Number of rows affected
   */
  public int create(List<UUID> accountIds) {
    int rowsAffected = 0;

    if (participantRepository.commonConversationExists(accountIds)) {
      return rowsAffected;
    }

    String title = "";
    for (int i = 0; i < accountIds.size(); i++) {
      title += accountService.findById(accountIds.get(i)).username();
      if (i < accountIds.size() - 1) {
        title += " & ";
      }
    }

    UUID conversationId = UUID.randomUUID();
    rowsAffected =
        conversationRepository.create(new Conversation(conversationId, title, LocalDateTime.now()));

    if (rowsAffected == 0) {
      return rowsAffected;
    }

    for (int i = 0; i < accountIds.size(); i++) {
      participantRepository.create(
          new Participant(accountIds.get(i), conversationId, LocalDateTime.now(), "ROLE_USER"));
    }

    return rowsAffected;
  }

  /**
   * Merge sort by recency
   *
   * @param list
   */
  private void sortByTime(List<Conversation> list) {
    if (list.size() >= 2) {

      int mid = list.size() / 2;

      List<Conversation> left = new ArrayList<>(list.subList(0, mid));
      List<Conversation> right = new ArrayList<>(list.subList(mid, list.size()));

      sortByTime(left); // recursive sortByTime where 'list' now becomes 'left'
      sortByTime(right);
      merge(list, left, right);
    }
  }

  private void merge(List<Conversation> list, List<Conversation> left, List<Conversation> right) {
    int i = 0, j = 0, k = 0;

    while (i < left.size() && j < right.size()) {
      if (left.get(i).lastSent().isAfter(right.get(j).lastSent())) {
        list.set(k++, left.get(i++));
      } else {
        list.set(k++, right.get(j++));
      }
    }
    while (i < left.size()) {
      list.set(k++, left.get(i++));
    }
    while (j < right.size()) {
      list.set(k++, right.get(j++));
    }
  }
}
