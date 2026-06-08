package com.webapp.example.account;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class AccountRepository {

        private final JdbcClient jdbcClient;

        public AccountRepository(JdbcClient jdbcClient) {
                this.jdbcClient = jdbcClient;
        }

        List<Account> findAll() {
                return jdbcClient
                                .sql("select id, username, email, password FROM account")
                                .query(Account.class)
                                .list();
        }

        // FIND account
        Optional<Account> findById(Integer id) {
                return jdbcClient.sql(
                                """
                                                SELECT id, username, email, password FROM Account WHERE id = :id
                                                """)
                                .param("id", id)
                                .query(Account.class)
                                .optional();
        }

        // FIND account by username
        Optional<Account> findById(String username) {
                return jdbcClient.sql(
                                """
                                                SELECT id, username, email, password FROM Account WHERE username = :username
                                                """)
                                .param(username)
                                .query(Account.class)
                                .optional();
        }

        // FIND account by username
        public Optional<Account> findByUsername(String username) {
                return jdbcClient.sql(
                                """
                                                SELECT id, username, email, password FROM Account WHERE username = :username
                                                """)
                                .param("username", username)
                                .query(Account.class)
                                .optional();
        }

        // CREATE account
        public void create(Account account) {
                int updated = jdbcClient.sql(
                                """
                                                INSERT INTO Account(
                                                id, username, email, password)
                                                values(?,?,?,?)
                                                """)
                                .params(List.of(account.id(), account.username(),
                                                account.email(), account.password()))
                                .update();

                Assert.state(updated == 1, "Failed to create account");
        }

        // Update account
        void update(Account account, Integer id) {
                int updated = jdbcClient.sql(
                                """
                                                update account set username = ?,
                                                email = ?, password = ? where id = ?)
                                                """)
                                .params(List.of(account.username(), account.email(),
                                                account.password(), id))
                                .update();

                Assert.state(updated == 1, "Failed to update account");
        }

        // DELETE account
        void delete(Integer id) {
                int updated = jdbcClient.sql(
                                """
                                                delete from account where id = :id
                                                """)
                                .param("id", id)
                                .update();

                Assert.state(updated == 1, "Failed to delete account");
        }

        public int count() {
                return jdbcClient.sql("select * from account")
                                .query()
                                .listOfRows()
                                .size();
        }
}
