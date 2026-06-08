package com.webapp.example.auth;

import com.webapp.example.account.Account;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

  private Account account;

  public UserPrincipal(Account account) {
    this.account = account;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("USER"));
  }

  @Override
  public String getPassword() {
    return account.password();
  }

  @Override
  public String getUsername() {
    return account.username();
  }

  public UUID getId() {
    return account.id();
  }
}
