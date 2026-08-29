package com.webapp.example.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.webapp.example.auth.JWTService;
import com.webapp.example.auth.MyUserDetailsService;
import com.webapp.example.auth.UserPrincipal;

@WebMvcTest(AccountController.class)
@DisplayName("Account Controller Tests")
class AccountControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean private JWTService jwtService;

  @MockitoBean private MyUserDetailsService myUserDetailsService;

  @MockitoBean private AccountRepository accountRepository;

  @Autowired private AccountController accountController;

  @Nested
  @DisplayName("Find By Username Segments Tests")
  class FindBySegmentTests {
    @Test
    void testFindByUsernameSegment() {
      List<AccountDTO> accounts =
          List.of(
              new AccountDTO(UUID.randomUUID(), "12345"),
              new AccountDTO(UUID.randomUUID(), "52341"));
      when(accountRepository.findByUsernameSegment("234")).thenReturn(accounts);
      assertEquals(accounts, accountController.findByUsernameSegment("234"));
    }

    @Test
    void testFindByUsernameSegmentAPI() throws Exception {
      UserPrincipal principal = mock(UserPrincipal.class);
      List<AccountDTO> accounts =
          List.of(
              new AccountDTO(UUID.randomUUID(), "12345"),
              new AccountDTO(UUID.randomUUID(), "52341"));
      when(accountRepository.findByUsernameSegment("234")).thenReturn(accounts);      
      mockMvc.perform(get("/accounts/search/{usernameSegment}", 234).with(user(principal))).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value(accounts.get(0).username()))
        .andExpect(jsonPath("$[1].username").value(accounts.get(1).username()));
    }

    @Test
    void testFindByZeroLengthUsernameSegment() {
      assertEquals(List.of(), accountController.findByUsernameSegment(""));
    }
  }


  @Nested
  @DisplayName("Get Current Account Id Tests")
  class GetCurrentAccountTests {
    @Test
    void testGetCurrentAccountId() {
        UUID id = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(id);
        assertEquals(principal.getId(), accountController.getCurrentAccountId(principal));
    }

    @Test
    void testGetCurrentAccountIdAPI() throws Exception {
        UUID id = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(id);
        mockMvc.perform(get("/accounts/me").with(user(principal))).andExpect(status().isOk())
          .andExpect(jsonPath("$").value(id.toString()));
    }
  }
}
