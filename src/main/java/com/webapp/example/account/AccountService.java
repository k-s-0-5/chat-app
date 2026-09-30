package com.webapp.example.account;

import com.webapp.example.Errors.AccountNotFoundException;
import com.webapp.example.auth.JWTService;
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

  /**
   * Finds account where account.id == id else throws AccountNotFoundException
   *
   * @param id
   * @return Account
   */
  public Account findById(UUID id) {
    return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
  }


  /**
   * Finds account where account.username == username else throws AccountNotFoundException
   *
   * @param username
   * @return Account
   */
  public Account findByUsername(String username) {
    return accountRepository
        .findByUsername(username)
        .orElseThrow(() -> new AccountNotFoundException(username));
  }

  /**
   * Finds id of account where account.username == username else throws AccountNotFoundException
   *
   * @param username
   * @return UUID
   */
  public UUID findIdByUsername(String username) {
    return accountRepository
        .findIdByUsername(username)
        .orElseThrow(() -> new AccountNotFoundException(username));
  }

  /**
   * Creates accounts
   *
   * @param signupRequest
   * @return Number of rows affected
   */
  public int register(SignupRequest signupRequest) {
    Account account =
        new Account(
            UUID.randomUUID(),
            signupRequest.username(),
            signupRequest.email(),
            encoder.encode(signupRequest.password()),
            "ROLE_USER");
    return accountRepository.create(account);
  }

  /**
   * Returns a JWT token if valid loginRequest else null
   *
   * @param loginRequest
   * @return String
   */
  public String verify(LoginRequest loginRequest) {
    Authentication authentication =
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()));
    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(loginRequest.username());
    } else {
      return null;
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
}
