package com.webapp.example.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.webapp.example.account.Account;

public class UserPrincipal implements UserDetails{

    private Account account;

    public UserPrincipal(Account account){
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

    public Integer getId() {
        return account.id();
    }
}
