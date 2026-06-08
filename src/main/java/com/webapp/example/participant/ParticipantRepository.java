package com.webapp.example.participant;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.webapp.example.account.Account;

@Repository
public class ParticipantRepository {

    private final JdbcClient jdbcClient;

    public ParticipantRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    List<Participant> findAll(Account account){
    return jdbcClient
            .sql("""
                SELECT * FROM Participant WHERE account_id = :accountId
                """
                )
            .param("accountId", account.id())
            .query(Participant.class)
            .list();
    }

    // CREATE participant

    public void create(Participant participant) {
        int updated = jdbcClient.sql(
                """
                        INSERT INTO Participant(
                        id, account_id, conversation_id, role)
                        values(?,?,?,?)
                        """)
                .params(participant.id(), participant.accountId(),
                        participant.conversationId(), participant.role())
                .update();

        Assert.state(updated == 1, "Failed to create message");
    }

    public int count() {
        return jdbcClient.sql("select * from message")
                .query()
                .listOfRows()
                .size();
    }

    public void saveAll(List<Participant> messages) {
        messages.forEach(this::create);
    }

}
