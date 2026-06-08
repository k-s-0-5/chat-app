package com.webapp.example.participant;

import com.webapp.example.account.Account;
import java.util.List;
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
   * Retrieves all participants
   *
   * @return List of all participants
   */
  List<Participant> findAllWithAccount(Account account) {
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
            id, account_id, conversation_id, role)
            values(?,?,?,?)
            """)
        .params(
            participant.id(), participant.accountId(),
            participant.conversationId(), participant.role())
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
            SELECT * FROM Participant
            """)
        .query()
        .listOfRows()
        .size();
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
