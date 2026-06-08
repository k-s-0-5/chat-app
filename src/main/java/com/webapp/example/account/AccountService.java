package com.webapp.example.account;

import com.webapp.example.auth.JWTService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final AuthenticationManager authManager;
  private final JWTService jwtService;
  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

  AccountService(
      AccountRepository accountRepository,
      AuthenticationManager authManager,
      JWTService jwtService) {
    this.accountRepository = accountRepository;
    this.authManager = authManager;
    this.jwtService = jwtService;
  }

  public Account findByUsername(String username) {
    Optional<Account> account = accountRepository.findByUsername(username);
    return account.get();
  }

  public Account findById(UUID id) {
    Optional<Account> account = accountRepository.findById(id);
    return account.get();
  }

  void register(Account account) {
    account =
        new Account(
            account.id(), account.username(), account.email(), encoder.encode(account.password()));
    accountRepository.create(account);
  }

  public String verify(Account account) {
    Authentication authentication =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(account.username(), account.password()));
    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(account.username());
    } else {
      return "Token validation unsuccessful";
    }
  }

  /*
      Method to bulk save accounts for testing purposes
  **/
  public void saveAll(List<Account> accounts) {
    accounts.forEach(this::register);
  }
}
