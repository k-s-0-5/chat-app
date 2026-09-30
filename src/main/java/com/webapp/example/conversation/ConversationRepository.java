package com.webapp.example.conversation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * This class encapsulates data access with the Database's Conversation table and can retrieve,
 * create, update or delete conversations. This class uses JDBC with SQL to manage data.
 */
@Repository
public class ConversationRepository {

  private final JdbcClient jdbcClient;

  public ConversationRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
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
   * Adds a new conversation to the database
   *
   * @param conversation
   * @return Number of rows affected
   */
  public int create(Conversation conversation) {
    return jdbcClient
        .sql(
            """
            INSERT INTO Conversation(
            id, title, last_sent)
            values(?,?,?)
            """)
        .params(conversation.id(), conversation.title(), conversation.lastSent())
        .update();
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
