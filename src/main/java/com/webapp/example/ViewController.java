package com.webapp.example;

import com.webapp.example.account.AccountService;
import com.webapp.example.account.LoginRequest;
import com.webapp.example.account.SignupRequest;
import com.webapp.example.auth.UserPrincipal;
import com.webapp.example.conversation.ConversationService;
import com.webapp.example.message.Message;
import com.webapp.example.message.MessageService;
import com.webapp.example.participant.ParticipantService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

  private final ParticipantService participantService;
  private final ConversationService conversationService;
  private final AccountService accountService;
  private final MessageService messageService;

  ViewController(
      ConversationService conversationService,
      AccountService accountService,
      MessageService messageService,
      ParticipantService participantService) {
    this.conversationService = conversationService;
    this.accountService = accountService;
    this.messageService = messageService;
    this.participantService = participantService;
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("loginRequest", new LoginRequest("", ""));
    return "login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("signupRequest", new SignupRequest("", "", ""));
    return "signup";
  }

  @GetMapping("/home")
  public String home(Model model, @AuthenticationPrincipal UserPrincipal principal) {
    model.addAttribute(
        "conversations",
        conversationService.getMyConversations(
            participantService.findAllWithAccount(principal.getId())));
    return "homepage";
  }

  @GetMapping("/conversation/{conversationId}/messages")
  public String getConversationMessages(
      @PathVariable UUID conversationId,
      @RequestParam(required = false) LocalDateTime before,
      @RequestParam(required = false) Long beforeid,
      Model model,
      @AuthenticationPrincipal UserPrincipal principal,
      jakarta.servlet.http.HttpServletRequest request) {

    if (!participantService.isAccountInConversation(principal.getId(), conversationId)) {
      return "redirect:/home";
    }
    List<Message> messages = new ArrayList<>();
    messages = messageService.findByConversationId(conversationId, before, beforeid).reversed();

    model.addAttribute("messages", messages);
    model.addAttribute("userId", principal.getId());

    String requestedWith = request.getHeader("X-Requested-With");
    if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
      return "homepage :: messageList";
    } else {
      model.addAttribute(
          "conversations",
          conversationService.getMyConversations(
              participantService.findAllWithAccount(principal.getId())));
      return "homepage";
    }
  }

  @PostMapping("/conversation")
  public String makeConversation(
      @RequestBody List<UUID> accountIds,
      Model model,
      HttpServletResponse response,
      @AuthenticationPrincipal UserPrincipal principal) {

    for (int i = 0; i < accountIds.size(); i++) {
      if (accountIds.get(i).equals(principal.getId())) {
        throw new IllegalArgumentException("Can not create a conversation with yourself");
      }
    }
    accountIds.add(principal.getId());
    if (participantService.commonConversationExists(accountIds)) {
      model.addAttribute(
          "conversations",
          conversationService.getMyConversations(
              participantService.findAllWithAccount(principal.getId())));
      return "homepage :: conversationList";
    }

    String title = "";
    for (int i = 0; i < accountIds.size(); i++) {
      title += accountService.findById(accountIds.get(i)).username();
      if (i < accountIds.size() - 1) {
        title += " | ";
      }
    }

    conversationService.create(title, accountIds);

    model.addAttribute(
        "conversations",
        conversationService.getMyConversations(
            participantService.findAllWithAccount(principal.getId())));
    return "homepage :: conversationList";
  }

  @PostMapping("/perform-login")
  public String login(
      @Valid @ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {
    String token = accountService.verify(loginRequest);

    if (token != null) {
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
  public String signup(
      @Valid @ModelAttribute SignupRequest signupRequest,
      BindingResult errors,
      HttpServletResponse response) {
    if (errors.hasErrors()) {
      return "signup";
    }

    accountService.register(signupRequest);
    String token =
        accountService.verify(new LoginRequest(signupRequest.username(), signupRequest.password()));

    if (token != null) {
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
