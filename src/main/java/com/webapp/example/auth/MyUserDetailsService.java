package com.webapp.example.auth;

import com.webapp.example.account.Account;
import com.webapp.example.account.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;

  MyUserDetailsService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByUsername(username).orElse(null);

    if (account == null) {
      throw new UsernameNotFoundException(username);
    }

    return new UserPrincipal(account);
  }
}
