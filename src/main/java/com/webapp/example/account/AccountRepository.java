package com.webapp.example.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * This class encapsulates data access with the Database's Account table and can retrieve, create,
 * update or delete accounts. This class uses JDBC with SQL to manage data.
 */
@Repository
public class AccountRepository {

  private final JdbcClient jdbcClient;

  public AccountRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  /**
   * Retrieves all accounts
   *
   * @return List of all accounts
   */
  public List<Account> findAll() {
    return jdbcClient
        .sql(
            """
            SELECT * FROM account
            """)
        .query(Account.class)
        .list();
  }

  /**
   * Retrieves account where account.id == id
   *
   * @param id
   * @return Optional of account
   */
  public Optional<Account> findById(UUID id) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Account WHERE id = :id
            """)
        .param("id", id)
        .query(Account.class)
        .optional();
  }

  /**
   * Retrieves account where account.username == username
   *
   * @param username
   * @return Optional of account
   */
  public Optional<Account> findByUsername(String username) {
    return jdbcClient
        .sql(
            """
            SELECT * FROM Account WHERE username = :username
            """)
        .param("username", username)
        .query(Account.class)
        .optional();
  }

  /**
   * Retrieves account id where account.username == username
   *
   * @param username
   * @return Optional of account id
   */
  public Optional<UUID> findIdByUsername(String username) {
    return jdbcClient
        .sql(
            """
            SELECT id FROM Account WHERE username = :username
            """)
        .param("username", username)
        .query(UUID.class)
        .optional();
  }

  /**
   * Retrieves account where account.username is like usernameSegment (excludes requester's id)
   *
   * @param usernameSegment
   * @return List of accounts
   */
  public List<AccountSearchResult> findByUsernameSegment(String usernameSegment, UUID id) {
    return jdbcClient
        .sql(
            """
            SELECT id, username FROM Account WHERE username LIKE ? AND id != ? LIMIT 7;
            """)
        .params("%" + usernameSegment + "%", id)
        .query(AccountSearchResult.class)
        .list();
  }

  /**
   * Adds a new account to the database
   *
   * @param account
   */
  public int create(Account account) {
    return jdbcClient
        .sql(
            """
            INSERT INTO Account(
            id, username, email, password, role)
            values(?,?,?,?,?)
            """)
        .params(
            List.of(
                account.id(),
                account.username(),
                account.email(),
                account.password(),
                account.role()))
        .update();
  }

  /**
   * Replaces account where account.id == id with updatedAccount
   *
   * @param updatedAccount
   * @param id
   */
  public int update(Account updatedAccount, UUID id) {
    return jdbcClient
        .sql(
            """
            UPDATE Account SET username = ?,
            email = ?, password = ?, role = ? WHERE id = ?
            """)
        .params(
            List.of(
                updatedAccount.username(),
                updatedAccount.email(),
                updatedAccount.password(),
                updatedAccount.role(),
                id))
        .update();
  }

  /**
   * Deletes account where account.id == id
   *
   * @param id
   */
  public int delete(UUID id) {
    return jdbcClient
        .sql(
            """
            DELETE FROM Account WHERE id = :id
            """)
        .param("id", id)
        .update();
  }

  /**
   * Retrieves number of rows in the Account table
   *
   * @return number of rows in the Account table
   */
  public int count() {
    return jdbcClient
        .sql(
            """
            SELECT COUNT(*) FROM Account
            """)
        .query(Integer.class)
        .single();
  }

  /**
   * Testing method, persists a list of accounts to the Account table
   *
   * @param accounts
   */
  public void saveAll(List<Account> accounts) {
    accounts.forEach(this::create);
  }
}
