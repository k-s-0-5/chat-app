package com.webapp.example.participant;

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
   * @param accountid
   * @param conversationId
   * @return true if the account is in the conversation
   */
  public boolean isAccountInConversation(UUID accountid, UUID conversationId) {
    return jdbcClient
        .sql(
            """
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
   * @param accountIds
   * @return true if a common conversation exists between all users
   */
  public boolean commonConversationExists(List<UUID> accountIds) {
    if (accountIds.isEmpty()) {
      return false;
    }
    return jdbcClient
        .sql(
            """
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
  public List<Participant> findAllWithAccount(UUID accountId) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Participant WHERE account_id = :accountId
            """)
        .param("accountId", accountId)
        .query(Participant.class)
        .list();
  }

  /**
   * Adds a new participant to the database
   *
   * @param participant
   * @return Number of rows affected
   */
  public int create(Participant participant) {
    return jdbcClient
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
   * @param updatedParticipant
   * @param id
   * @return Number of rows affected
   */
  public int update(Participant updatedParticipant, Integer id) {
    return jdbcClient
        .sql(
            """
            UPDATE Participant SET last_read = ?, role = ? WHERE account_id = ? AND conversation_id = ?
            """)
        .params(
            updatedParticipant.lastRead(),
            updatedParticipant.role(),
            updatedParticipant.accountId(),
            updatedParticipant.conversationId())
        .update();
  }

  /**
   * Deletes participant where participant.id = id
   *
   * @param id
   * @return Number of rows affected
   */
  public int delete(UUID accountId, UUID conversationId) {
    return jdbcClient
        .sql(
            """
            DELETE FROM Participant WHERE account_id = ? AND conversation_id = ?
            """)
        .params(accountId, conversationId)
        .update();
  }
}
