package com.webapp.example.account;

import com.webapp.example.Errors.AccountNotFoundException;
import com.webapp.example.auth.JWTService;
import java.util.List;
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
    return accountRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));
  }

  public Account findById(UUID id) {
    return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
  }

  public void register(SignupRequest signupRequest) {
    Account account =
        new Account(
            UUID.randomUUID(),
            signupRequest.username(),
            signupRequest.email(),
            encoder.encode(signupRequest.password()),
            "ROLE_USER");
    accountRepository.create(account);
  }

  public String verify(SignupRequest signupRequest) {
    Authentication authentication =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(signupRequest.username(), signupRequest.password()));
    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(signupRequest.username());
    } else {
      return "";
    }
  }

  public void testRegister(Account account) {
    account =
        new Account(
            account.id(),
            account.username(),
            account.email(),
            encoder.encode(account.password()),
            account.role());
    accountRepository.create(account);
  }

  /*
      Method to bulk save accounts for testing purposes
  **/
  public void saveAll(List<Account> accounts) {
    accounts.forEach(this::testRegister);
  }
}
