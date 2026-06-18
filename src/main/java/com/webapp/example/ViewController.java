package com.webapp.example;

import com.webapp.example.account.Account;
import com.webapp.example.account.AccountService;
import com.webapp.example.auth.UserPrincipal;
import com.webapp.example.conversation.ConversationService;
import com.webapp.example.message.Message;
import com.webapp.example.message.MessageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {

  private final ConversationService conversationService;
  private final AccountService accountService;
  private final MessageService messageService;

  ViewController(
      ConversationService conversationService,
      AccountService accountService,
      MessageService messageService) {
    this.conversationService = conversationService;
    this.accountService = accountService;
    this.messageService = messageService;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @GetMapping("/home")
  public String home(Model model, @AuthenticationPrincipal UserPrincipal principal) {
    Account account = accountService.findByUsername(principal.getUsername());
    model.addAttribute("conversations", conversationService.getMyConversations(account));
    return "homepage";
  }

  @GetMapping("/conversation/{conversationId}/messages")
  public String getConversationMessages(
      @PathVariable UUID conversationId,
      Model model,
      @AuthenticationPrincipal UserPrincipal principal) {
    List<Message> messages = messageService.findByConversationId(conversationId);
    model.addAttribute("messages", messages);
    model.addAttribute("currentUserId", principal.getId());

    return "homepage :: messageList";
  }

  @PostMapping("/perform-login")
  public String login(@ModelAttribute Account account, HttpServletResponse response) {
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

  @PostMapping("/perform-signup")
  public String signup(@ModelAttribute Account account, HttpServletResponse response) {
    accountService.register(account);
    String token = accountService.verify(account);

    if (token != null && !token.isEmpty()) {
      Cookie jwtCookie = new Cookie("jwt_token", token);
      jwtCookie.setHttpOnly(true);
      jwtCookie.setPath("/");
      response.addCookie(jwtCookie);
      return "redirect:/home";
    } else {
      return "redirect:/signup";
    }
  }
}
