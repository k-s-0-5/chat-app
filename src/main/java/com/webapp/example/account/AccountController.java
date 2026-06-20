package com.webapp.example.account;

import com.webapp.example.auth.UserPrincipal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountRepository accountRepository;

  public AccountController(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @GetMapping("/me")
  public UUID getCurrentAccountId(@AuthenticationPrincipal UserPrincipal principal) {
    return principal.getId();
  }

  @GetMapping("/search/{usernameSegment}")
  public List<Account> findByUsernameSegment(@PathVariable String usernameSegment) {
    if (usernameSegment.isEmpty()) {
      return List.of();
    }
    return accountRepository.findByUsernameSegment(usernameSegment);
  }

  @GetMapping("/{username}")
  Account findByUsername(@PathVariable String username) {
    Optional<Account> account = accountRepository.findByUsername(username);
    if (account.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return account.get();
  }

  @GetMapping("/{id}")
  Account findById(@PathVariable UUID id) {
    Optional<Account> account = accountRepository.findById(id);
    if (account.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return account.get();
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/{id}")
  void update(@RequestBody Account account, @PathVariable UUID id) {
    accountRepository.update(account, id);
  }
}
