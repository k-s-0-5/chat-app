package com.webapp.example.account;

import com.webapp.example.auth.UserPrincipal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for accounts */
@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountRepository accountRepository;

  public AccountController(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  /**
   * Retrieves the id of the currently logged in user
   *
   * @return Id of logged in user
   */
  @ResponseStatus(HttpStatus.OK) 
  @GetMapping("/me")
  public UUID getCurrentAccountId(@AuthenticationPrincipal UserPrincipal principal) {
    return principal.getId();
  }

  /**
   * Retrieves limited list of accounts where the account's username contains the username segment
   *
   * @param usernameSegment
   * @return returns a short list of accounts
   */
  @ResponseStatus(HttpStatus.OK) 
  @GetMapping("/search/{usernameSegment}")
  public List<Account> findByUsernameSegment(@PathVariable String usernameSegment) {
    if (usernameSegment.isEmpty()) {
      return List.of();
    }
    return accountRepository.findByUsernameSegment(usernameSegment);
  }
}
