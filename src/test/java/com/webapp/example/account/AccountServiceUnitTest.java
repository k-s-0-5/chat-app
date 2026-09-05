package com.webapp.example.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.webapp.example.Errors.AccountNotFoundException;
import com.webapp.example.auth.JWTService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
public class AccountServiceUnitTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JWTService jwtService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Find Tests")
    class FindTests {
        @Test
        void testFindById() {
            UUID id = UUID.randomUUID();
            Account account = new Account(id, "Test", "test@mail.com", encoder.encode("TestPassword"), "ROLE_USER");
            when(accountRepository.findById(id)).thenReturn(Optional.of(account));
            Account result = accountService.findById(id);
            assertEquals(account, result);
        }

        @Test
        void testFindByNonExistentId() {
            UUID id = UUID.randomUUID();
            when(accountRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(AccountNotFoundException.class, () -> accountService.findById(id));
        }

        @Test
        void testFindByUsername() {
            String username = "Test";
            Account account = new Account(UUID.randomUUID(), username, "test@mail.com", encoder.encode("TestPassword"), "ROLE_USER");
            when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
            Account result = accountService.findByUsername(username);
            assertEquals(account, result);
        }

        @Test
        void testFindByNonExistentUsername() {
            String username = "Test";
            when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());
            assertThrows(AccountNotFoundException.class, () -> accountService.findByUsername(username));
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {
        @Test
        void testRegister() {
            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
            SignupRequest signupRequest = new SignupRequest("Test", "test@mail.com", "TestPassword");
            accountService.register(signupRequest);
            verify(accountRepository).create(accountCaptor.capture());
            assertEquals(signupRequest.username(), accountCaptor.getValue().username());
        }
    }

    @Nested
    @DisplayName("Verify Tests")
    class VerifyTests {
        @Test
        void testVerifySuccess() {
            LoginRequest loginRequest = new LoginRequest("Test", encoder.encode("TestPassword"));
            Authentication authentication = mock(Authentication.class);
            when(authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()))).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            String jwt = accountService.verify(loginRequest);
            assertNotEquals(jwt, "");
        }

        @Test
        void testVerifyFail() {
            LoginRequest loginRequest = new LoginRequest("Test", "TestPassword");
            Authentication authentication = mock(Authentication.class);
            when(authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()))).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);
            String jwt = accountService.verify(loginRequest);
            assertEquals(jwt, "");
        }
    }
}
