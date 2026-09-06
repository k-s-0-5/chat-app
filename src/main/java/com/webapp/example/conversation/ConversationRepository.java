package com.webapp.example.conversation;

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
   */
  public void create(Conversation conversation) {
    jdbcClient
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
   * Replaces conversation where conversation.id = id with updatedConversation
   *
   * @param conversation
   * @param id
   */
  public void update(Conversation updatedConversation, UUID id) {
    jdbcClient
        .sql(
            """
            UPDATE Conversation SET title = ?,
            last_sent = ? where id = ?
            """)
        .params(updatedConversation.title(), updatedConversation.lastSent(), id)
        .update();
  }

  /**
   * Deletes conversation where conversation.id = id
   *
   * @param id
   */
  public void delete(UUID id) {
    jdbcClient
        .sql(
            """
            DELETE FROM Conversation WHERE id = :id
            """)
        .param("id", id)
        .update();
  }

  /**
   * Retrieves number of rows in the Conversation table
   *
   * @return number of rows in the Conversation table
   */
  public int count() {
    return jdbcClient
        .sql(
            """
            SELECT COUNT(*) FROM Conversation
            """)
        .query(Integer.class)
        .single();
  }

  /**
   * Testing method, persists a list of conversations to the Conversation table
   *
   * @param conversations
   */
  public void saveAll(List<Conversation> conversations) {
    conversations.forEach(this::create);
  }
}
