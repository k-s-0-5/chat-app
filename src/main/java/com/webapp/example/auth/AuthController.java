package com.webapp.example.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.webapp.example.account.Account;
import com.webapp.example.account.AccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PostMapping("/perform-login")
    public String login(@Valid @ModelAttribute Account account, HttpServletResponse response) {
        String token = accountService.verify(account);

        if (token != null && !token.isEmpty()) {
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
            return "redirect:/home";
        } else {
            return "redirect:/login";
        }
    }
}
