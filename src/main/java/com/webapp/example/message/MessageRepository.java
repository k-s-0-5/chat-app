package com.webapp.example.message;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * This class encapsulates data access with the Database's Message table and can retrieve, create,
 * update or delete messages. This class uses JDBC with SQL to manage data.
 */
@Repository
public class MessageRepository {

  private final JdbcClient jdbcClient;

  public MessageRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  /**
   * Retrieves all messages
   *
   * @return List of all messages
   */
  public List<Message> findAll() {
    return jdbcClient.sql("SELECT * FROM Message").query(Message.class).list();
  }

  /**
   * Retrieves message where message.id == id
   *
   * @param id
   * @return Optional of message
   */
  public Optional<Message> findById(Integer id) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Message WHERE id = :id
            """)
        .param("id", id)
        .query(Message.class)
        .optional();
  }

  /**
   * Retrieves all messages within a conversation
   *
   * @param conversationId
   * @return List of messages that with a conversationId == conversationId
   */
  public List<Message> findByConversationId(Integer conversationId) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Message WHERE conversation_id = :conversationId
            """)
        .param("conversationId", conversationId)
        .query(Message.class)
        .list();
  }

  /**
   * Adds a new message to the database
   *
   * @param message
   */
  public void create(Message message) {
    jdbcClient
        .sql(
            """
            INSERT INTO Message(
            account_id, conversation_id,
            sent_at, content_type,
            contents, attachment_url,
            is_read, edited)
            values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """)
        .params(
            message.accountId(),
            message.conversationId(),
            message.sentAt(),
            message.contentType().toString(),
            message.contents(),
            message.attachmentUrl(),
            message.isRead(),
            message.edited())
        .update();
  }

  /**
   * Replaces message where message.id = id with updatedMessage
   *
   * @param message
   * @param id
   */
  public void update(Message updatedMessage, Integer id) {
    jdbcClient
        .sql(
            """
            UPDATE Message set
            account_id = ?, conversation_id = ?,
            sent_at = ?, content_type = ?,
            contents = ?, attachment_url = ?,
            is_read = ?, edited = ?
            where id = ?)
            """)
        .params(
            List.of(
                updatedMessage.accountId(),
                updatedMessage.conversationId(),
                updatedMessage.sentAt(),
                updatedMessage.contentType(),
                updatedMessage.contents(),
                updatedMessage.attachmentUrl(),
                updatedMessage.isRead(),
                updatedMessage.edited(),
                id))
        .update();
  }

  /**
   * Deletes message where message.id = id
   *
   * @param id
   */
  public void delete(Integer id) {
    jdbcClient
        .sql(
            """
            DELETE FROM Message WHERE id = :id
            """)
        .param("id", id)
        .update();
  }

  /**
   * Retrieves number of rows in the Message table
   *
   * @return number of rows in the Message table
   */
  public int count() {
    return jdbcClient.sql("SELECT * FROM Message").query().listOfRows().size();
  }

  /**
   * Testing method, persists a list of messages to the Message table
   *
   * @param messages
   */
  public void saveAll(List<Message> messages) {
    messages.forEach(this::create);
  }
}
