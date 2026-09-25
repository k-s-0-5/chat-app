package com.webapp.example.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// https://www.innoq.com/en/blog/2023/10/spring-boot-testing/@JdbcTest  
@JdbcTest 
@Import(AccountRepository.class)
@DisplayName("Account Repository Tests")
public class AccountRepositoryIntegrationTest {

    @Autowired private AccountRepository accountRepository;

    @MockitoBean private CommandLineRunner clr; // To avoid running the clr in Application.java

    @Nested
    @DisplayName("Account REST tests")
    class FindBySegmentTests {
        @Test
        void testCreateAndFindById() {
            UUID id = UUID.randomUUID();
            Account a = new Account(id, "user1", "user1@example.com", "1", "ROLE_USER");
            accountRepository.create(a);
            Optional<Account> oa = accountRepository.findById(id);
            assertTrue(oa.isPresent());
            assertEquals(oa.get(), a);
        }

        @Test
        void testDelete() {
            UUID id = UUID.randomUUID();
            Account a = new Account(id, "user1", "user1@example.com", "1", "ROLE_USER");
            accountRepository.create(a);
            int rowsAffected = accountRepository.delete(id);
            assertEquals(rowsAffected, 1);
        }

        @Test
        void testUpdate() {
            UUID id = UUID.randomUUID();
            Account a1 = new Account(id, "user1", "user1@example.com", "1", "ROLE_USER");
            accountRepository.create(a1);
            Account a2 = new Account(id, "user2", "user2@example.com", "2", "ROLE_USER");
            int rowsAffected = accountRepository.update(a2, id);
            assertEquals(rowsAffected, 1);
        }
    }
}
