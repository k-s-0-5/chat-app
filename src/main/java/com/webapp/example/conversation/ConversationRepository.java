package com.webapp.example.conversation;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ConversationRepository {

    private final JdbcClient jdbcClient;

    public ConversationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<Conversation> findAll() {
        return jdbcClient
                .sql("select * from conversation")
                .query(Conversation.class)
                .list();
    }

    /*
     * Finds conversation by Id
     **/
    Optional<Conversation> findById(Integer id) {
        return jdbcClient.sql(
                """
                        SELECT * FROM Conversation WHERE id = :id
                        """)
                .param("id", id)
                .query(Conversation.class)
                .optional();
    }

    /*
     * Creates a conversation
     **/
    public void create(Conversation conversation) {
        int updated = jdbcClient.sql(
                """
                        INSERT INTO Conversation(
                        id, title, lastSent)
                        values(?,?,?)
                        """)
                .params(conversation.id(), conversation.title(),
                        conversation.lastSent())
                .update();

        Assert.state(updated == 1, "Failed to create conversation");
    }

    /*
     * Updates a conversation's title, and lastSent date
     **/
    void update(Conversation conversation, Integer id) {
        int updated = jdbcClient.sql(
                """
                        update conversation set title = ?,
                        lastSent = ? where id = ?
                        """)
                .params(conversation.title(), conversation.lastSent(), id)
                .update();

        Assert.state(updated == 1, "Failed to update conversation");
    }

    /*
     * Adds a participant to a conversation
     **/
    void addParticipant(Integer conversationId, Integer accountId) {
        int updated = jdbcClient.sql(
                """
                        INSERT INTO Participant(account_id, conversation_id)
                        VALUES(?, ?)
                        """)
                .params(conversationId, accountId)
                .update();

        Assert.state(updated == 1, "Failed to add participant");
    }

    /*
     * Removes a participant from a conversation
     **/
    void removeParticipant(Integer conversationId, Integer accountId) {

        int updated = jdbcClient.sql(
                """
                        delete from Participant where conversation_id = ? AND account_id = ?
                        """)
                .params(conversationId, accountId)
                .update();

        Assert.state(updated == 1, "Failed to remove participant");
    }

    /*
     * Deletes conversation
     **/
    void delete(Integer id) {
        int updated = jdbcClient.sql(
                """
                        delete from conversation where id = :id
                        """)
                .param("id", id)
                .update();

        Assert.state(updated == 1, "Failed to delete conversation");
    }

    /*
     * Retrieves count of rows in table
     **/
    public int count() {
        return jdbcClient.sql("select * from conversation")
                .query()
                .listOfRows()
                .size();
    }

    public void saveAll(List<Conversation> conversations) {
        conversations.forEach(this::create);
    }
}
