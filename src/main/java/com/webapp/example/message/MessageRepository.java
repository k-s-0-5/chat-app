package com.webapp.example.message;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.jdbc.core.simple.JdbcClient;

/* 
This class encapsulates data access with the Database's Message table and can retrieve, create, update or delete messages.

This class uses JDBC with SQL to manage data.
*/
@Repository
public class MessageRepository {

    private final JdbcClient jdbcClient;

    public MessageRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<Message> findAll() {
        return jdbcClient
                .sql("select * from message")
                .query(Message.class)
                .list();
    }

    // FIND message
    Optional<Message> findById(Integer id) {
        return jdbcClient.sql(
                """
                        SELECT * FROM Message WHERE id = :id
                        """)
                .param("id", id)
                .query(Message.class)
                .optional();
    }

    // FIND message by conversation
    List<Message> findByConversationId(Integer conversationId) {
        return jdbcClient.sql(
                """
                        SELECT * FROM Message WHERE conversation_id = :conversationId
                        """)
                .param("conversationId", conversationId)
                .query(Message.class)
                .list();
    }


    // CREATE message
    public void create(Message message) {
        int updated = jdbcClient.sql(
                """
                        INSERT INTO Message(
                        id, account_id, conversation_id, sent_at, content_type, contents,
                        attachment_url, tag, is_read, edited)
                        values(?,?,?,?,?,?,?,?,?,?)
                        """)
                .params(message.id(), message.accountId(),
                        message.conversationId(), message.sentAt(),
                        message.contentType().toString(), message.contents(),
                        message.attachmentUrl(), message.tag(),
                        message.isRead(), message.edited())
                .update();

        Assert.state(updated == 1, "Failed to create message");
    }

    // Update message
    void update(Message message, Integer id) {
        int updated = jdbcClient.sql(
                """
                        update message set
                        account_id = ?, conversation_id = ?,
                        sent_at = ?, content_type = ?,
                        contents = ?, attachment_url = ?,
                        tag = ?, is_read = ?,
                        edited = ? where id = ?)
                        """)
                .params(List.of(message.accountId(), message.conversationId(),
                        message.sentAt(), message.contentType(),
                        message.contents(), message.attachmentUrl(),
                        message.tag(), message.isRead(),
                        message.edited(), id))
                .update();

        Assert.state(updated == 1, "Failed to update message");
    }

    // DELETE message
    void delete(Integer id) {
        int updated = jdbcClient.sql(
                """
                        delete from message where id = :id
                        """)
                .param("id", id)
                .update();

        Assert.state(updated == 1, "Failed to delete message");
    }

    public int count() {
        return jdbcClient.sql("select * from message")
                .query()
                .listOfRows()
                .size();
    }

    public void saveAll(List<Message> messages) {
        messages.forEach(this::create);
    }
}
