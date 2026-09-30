package com.webapp.example.conversation;

import com.webapp.example.Errors.ConversationNotFoundException;
import com.webapp.example.account.Account;
import com.webapp.example.participant.Participant;
import com.webapp.example.participant.ParticipantRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

  /**
   * Retrieves conversation where conversation.id == id
   *
   * @param id
   * @return Conversation
   */
  public Conversation findById(UUID id) {
    return conversationRepository.findById(id).orElseThrow(() -> new ConversationNotFoundException(id));
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
      conversations.add(conversationRepository.findById(p.conversationId()).get());
    }
    sortByTime(conversations);
    return conversations;
  }

  /**
   * Creates conversation, returns early if conversation with all accounts already exists
   *
   * @param accounts
   * @return Number of rows affected
   */
  public int create(List<Account> accounts) {
    int rowsAffected = 0;
    List<UUID> accountIds = new ArrayList<UUID>();
    for (Account account : accounts) {
      accountIds.add(account.id());
    }
    if (participantRepository.commonConversationExists(accountIds)) {
      return rowsAffected;
    }

    String title = "";
    for (int i = 0; i < accounts.size(); i++) {
      title += accounts.get(i).username();
      if (i < accounts.size() - 1) {
        title += " & ";
      }
    }

    UUID conversationId = UUID.randomUUID();
    rowsAffected = conversationRepository.create(new Conversation(conversationId, title, LocalDateTime.now()));

    if (rowsAffected == 0){
      return rowsAffected;
    }

    for (int i = 0; i < accounts.size(); i++) {
      participantRepository.create(
          new Participant(
              accounts.get(i).id(),
              conversationId,
              LocalDateTime.now(),
              "ROLE_USER"));
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
  private void merge(
      List<Conversation> list, List<Conversation> left, List<Conversation> right) {
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
