package com.webapp.example.conversation;

import com.webapp.example.participant.Participant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class encapsulates data access with the Database's Conversation table and can retrieve,
 * create, update or delete conversations. This class uses JDBC with SQL to manage data.
 */
@Repository
public class ConversationRepository {

  private final JdbcClient jdbcClient;
  private final JdbcTemplate jdbcTemplate;

  public ConversationRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
    this.jdbcClient = jdbcClient;
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieves all conversations
   *
   * @return List of all conversations
   */
  public List<Conversation> findAll() {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Conversation
            """)
        .query(Conversation.class)
        .list();
  }

  /**
   * Retrieves conversation where conversation.id == id
   *
   * @param id
   * @return Optional of conversation
   */
  public Optional<Conversation> findById(UUID id) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Conversation WHERE id = :id
            """)
        .param("id", id)
        .query(Conversation.class)
        .optional();
  }

  /**
   * Creates conversation and participants
   *
   * @param conversation
   * @param participants
   * @return Conversation
   */
  @Transactional
  public Conversation create(Conversation conversation, List<Participant> participants) {
    jdbcClient
        .sql(
            """
            INSERT INTO Conversation(
            id, title, last_sent)
            values(?,?,?)
            """)
        .params(conversation.id(), conversation.title(), conversation.lastSent())
        .update();

    jdbcTemplate.batchUpdate(
        """
        INSERT INTO Participant(account_id, conversation_id, last_read, role) values (?,?,?,?)
        """,
        participants,
        100,
        (ps, p) -> {
          ps.setObject(1, p.accountId());
          ps.setObject(2, p.conversationId());
          ps.setObject(3, p.lastRead());
          ps.setString(4, p.role());
        });
        return conversation;
  }

  /**
   * Deletes conversation where conversation.id = id
   *
   * @param id
   * @return Number of rows affected
   */
  public int delete(UUID id) {
    return jdbcClient
        .sql(
            """
            DELETE FROM Conversation WHERE id = :id
            """)
        .param("id", id)
        .update();
  }
}
