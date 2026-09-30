package com.webapp.example.message;

import com.webapp.example.config.CryptoUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class encapsulates data access with the Database's Message table and can retrieve, create,
 * update or delete messages. This class uses JDBC with SQL to manage data.
 */
@Repository
public class MessageRepository {

  private final JdbcClient jdbcClient;
  private final CryptoUtils cryptoUtils;

  public MessageRepository(JdbcClient jdbcClient, CryptoUtils cryptoUtils) {
    this.jdbcClient = jdbcClient;
    this.cryptoUtils = cryptoUtils;
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

  //   https://learncodewithdurgesh.com/tutorials/spring-boot-tutorials/rowmapper-in-spring-jdbc
  /**
   * Retrieves messages within a conversation sent before timestamp capped at 20
   *
   * @param conversationId
   * @param timestamp
   * @return List of messages with a conversationId == conversationId
   */
  public List<Message> findByConversationId(UUID conversationId, LocalDateTime timestamp) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Message WHERE conversation_id = ? AND sent_at <= ? ORDER BY sent_at DESC LIMIT 20
            """)
        .params(conversationId, timestamp)
        .query(
            (rs, rowNumber) ->
                new Message(
                    rs.getLong("id"),
                    UUID.fromString(rs.getString("account_id")),
                    UUID.fromString(rs.getString("conversation_id")),
                    rs.getTimestamp("sent_at").toLocalDateTime(),
                    cryptoUtils.decrypt(rs.getString("contents")),
                    rs.getBoolean("edited")))
        .list();
  }

  /**
   * Retrieves messages within a conversation sent before timestamp capped at 20 (excludes message
   * with id)
   *
   * @param conversationId
   * @param timestamp
   * @param id
   * @return List of messages with a conversationId == conversationId
   */
  public List<Message> findByConversationId(UUID conversationId, LocalDateTime timestamp, long id) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Message WHERE conversation_id = ? AND sent_at <= ? AND id != ? ORDER BY sent_at DESC LIMIT 20
            """)
        .params(conversationId, timestamp, id)
        .query(
            (rs, rowNumber) ->
                new Message(
                    rs.getLong("id"),
                    UUID.fromString(rs.getString("account_id")),
                    UUID.fromString(rs.getString("conversation_id")),
                    rs.getTimestamp("sent_at").toLocalDateTime(),
                    cryptoUtils.decrypt(rs.getString("contents")),
                    rs.getBoolean("edited")))
        .list();
  }

  /**
   * Transaction: Adds a new message to the database, updates conversation last_sent
   *
   * @param messageCreateRequest
   * @param accountId
   * @return Created message
   */
  @Transactional 
  public Message create(MessageCreateRequest messageCreateRequest, UUID accountId) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    LocalDateTime now = LocalDateTime.now();
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
            now,
            cryptoUtils.encrypt(messageCreateRequest.contents()),
            false)
        .update(keyHolder);

    jdbcClient
        .sql(
            """
            UPDATE Conversation SET
            last_sent = ? WHERE id = ?
            """)
        .params(now, messageCreateRequest.conversationId())
        .update();
    Long id = keyHolder.getKey().longValue();
    return new Message(
        id,
        accountId,
        messageCreateRequest.conversationId(),
        LocalDateTime.now(),
        messageCreateRequest.contents(),
        false);
  }

  /**
   * Updates message's contents where message.id = originalMessage.id and message.accountId =
   * originalMessage.accountId
   *
   * @param originalMessage
   * @param content
   * @return Updated message
   */
  public Message update(Message originalMessage, String content) {
    jdbcClient
        .sql(
            """
            UPDATE Message SET
            contents = ?, edited = ?
            WHERE id = ? AND account_id = ?
            """)
        .params(
            cryptoUtils.encrypt(content), true, originalMessage.id(), originalMessage.accountId())
        .update();
    return new Message(
        originalMessage.id(),
        originalMessage.accountId(),
        originalMessage.conversationId(),
        originalMessage.sentAt(),
        content,
        true);
  }

  /**
   * Deletes message where message.id = id and message.accountId = accountId
   *
   * @param id
   * @param accountId
   * @return Number of affected rows
   */
  public int delete(long id, UUID accountId) {
    return jdbcClient
        .sql(
            """
            DELETE FROM Message WHERE id = ? AND account_id = ?
            """)
        .params(id, accountId)
        .update();
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
            cryptoUtils.encrypt(message.contents()),
            false)
        .update(keyHolder);
    Long id = keyHolder.getKey().longValue();
    return new Message(
        id,
        message.accountId(),
        message.conversationId(),
        message.sentAt(),
        message.contents(),
        false);
  }
}
