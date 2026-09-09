package com.webapp.example.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
  public Optional<Message> findById(long id) {
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
  public List<Message> findByConversationId(UUID conversationId) {
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
  public Message create(MessageCreateRequest messageCreateRequest, UUID accountId) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcClient
        .sql(
            """
            INSERT INTO Message(
            account_id, conversation_id, 
            sent_at, contents, 
            edited)
            values(?, ?, ?, ?, ?)
            """)
        .params(
            accountId,
            messageCreateRequest.conversationId(),
            LocalDateTime.now(),
            messageCreateRequest.contents(),
            false
            )
        .update(keyHolder);
      Long id = keyHolder.getKey().longValue();
      return new Message(id, accountId, messageCreateRequest.conversationId(), LocalDateTime.now(), messageCreateRequest.contents(), false);
  }

  /**
   * Updates message where message.id = id and message.accountId = originalMessage.accountId
   *
   * @param message
   * @param id
   */
  public Message update(Message message, String content) {
    jdbcClient
        .sql(
            """
            UPDATE Message set
            contents = ?, edited = ? 
            WHERE id = ? AND account_id = ?
            """)
        .params(
            List.of(
                content, true,
                message.id(), message.accountId()))
        .update();
      return new Message(message.id(), message.accountId(), message.conversationId(), message.sentAt(), content, true);
  }

  /**
   * Deletes message where message.id = id
   *
   * @param id
   */
  public int delete(long id, UUID accountId) {
    return jdbcClient
        .sql(
            """
            DELETE FROM Message WHERE id = ? AND account_id = ?
            """)
        .params(
            List.of(id, accountId))
        .update();
  }

  /**
   * Retrieves number of rows in the Message table
   *
   * @return number of rows in the Message table
   */
  public int count() {
    return jdbcClient.sql("SELECT COUNT(*) FROM Message").query(Integer.class).single();
  }

  /**
   * Testing method, persists a list of messages to the Message table
   *
   * @param messages
   */
  public void saveAll(List<Message> messages) {
    messages.forEach(this::testCreate);
  }

  public Message testCreate(Message message) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcClient
        .sql(
            """
            INSERT INTO Message(
            account_id, conversation_id, 
            sent_at, contents, 
            edited)
            values(?, ?, ?, ?, ?)
            """)
        .params(
            message.accountId(),
            message.conversationId(),
            message.sentAt(),
            message.contents(),
            false
            )
        .update(keyHolder);
      Long id = keyHolder.getKey().longValue();
      return new Message(id, message.accountId(),  message.conversationId(),  message.sentAt(),  message.contents(), false);
  }
}
