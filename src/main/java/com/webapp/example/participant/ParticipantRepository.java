package com.webapp.example.participant;

import com.webapp.example.account.Account;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * This class encapsulates data access with the Database's Participant table and can retrieve,
 * create, update or delete participants. This class uses JDBC with SQL to manage data.
 */
@Repository
public class ParticipantRepository {

  private final JdbcClient jdbcClient;

  public ParticipantRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

 /**
   * Returns true if the account is in the conversation
   *
   * @param accountid
   * @param conversationId
   * @return boolean
   */
  public boolean isAccountInConversation(UUID accountid, UUID conversationId) {
    return jdbcClient
    .sql("""
        SELECT EXISTS (
            SELECT *
            FROM Participant 
            WHERE account_id = :accountId
            AND conversation_id = :conversationId 
        )
        """)
    .param("accountId", accountid)
    .param("conversationId", conversationId)
    .query(Boolean.class) 
    .single();
  }

 /**
   * Returns true if a common conversation exists between all users
   *
   * @param accountIds
   * @return boolean
   */
  public boolean commonConversationExists(List<UUID> accountIds) {
    if (accountIds.isEmpty()) {
        return false; 
    }
    return jdbcClient
    .sql("""
        SELECT EXISTS (
            SELECT COUNT(*)
            FROM Participant 
            WHERE account_id IN (:accountIds)
            GROUP BY conversation_id 
            HAVING COUNT(DISTINCT account_id) = :accountCount
        )
        """)
    .param("accountIds", accountIds)
    .param("accountCount", accountIds.size())
    .query(Boolean.class) 
    .single();
  }

  /**
   * Retrieves all participants
   *
   * @return List of all participants
   */
  public List<Participant> findAllWithAccount(Account account) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Participant WHERE account_id = :accountId
            """)
        .param("accountId", account.id())
        .query(Participant.class)
        .list();
  }

  /**
   * Adds a new participant to the database
   *
   * @param participant
   */
  public void create(Participant participant) {
    jdbcClient
        .sql(
            """
            INSERT INTO Participant(
            account_id, conversation_id, last_read, role)
            values(?,?,?,?)
            """)
        .params(
            participant.accountId(), participant.conversationId(), 
            participant.lastRead(), participant.role())
        .update();
  }

  /**
   * Replaces participant where participant.id = id with updatedParticipant
   *
   * @param participant
   * @param id
   */
  public void update(Participant updatedParticipant, Integer id) {
    jdbcClient
        .sql(
            """
            UPDATE Conversation SET accountId = ?,
            conversationId = ?, last_read = ?, role = ? where id = ?
            """)
        .params(
            updatedParticipant.accountId(),
            updatedParticipant.conversationId(),
            updatedParticipant.lastRead(),
            updatedParticipant.role(),
            id)
        .update();
  }

  /**
   * Deletes participant where participant.id = id
   *
   * @param id
   */
  public void delete(Participant participant) {
    jdbcClient
        .sql(
            """
            DELETE FROM Participant WHERE participant_id = :participant_id
            """)
        .param("participant_id", participant.id())
        .update();
  }

  /**
   * Retrieves number of rows in the Participant table
   *
   * @return number of rows in the Participant table
   */
  public int count() {
    return jdbcClient
        .sql(
            """
            SELECT COUNT(*) FROM Participant
            """)
        .query(Integer.class)
        .single();
  }

  /**
   * Testing method, persists a list of Participants to the Participant table
   *
   * @param participants
   */
  public void saveAll(List<Participant> participants) {
    participants.forEach(this::create);
  }
}
