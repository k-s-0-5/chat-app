package com.webapp.example.config;

import com.webapp.example.auth.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;

public class JwtSuccessHandler {

  private final JWTService jwtService;

  JwtSuccessHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication,
      JWTService jwtService)
      throws IOException {
    this.jwtService = jwtService;
    String token = this.jwtService.generateToken(authentication.getName());

    Cookie jwtCookie = new Cookie("jwt_token", token);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    response.addCookie(jwtCookie);

    response.sendRedirect("/home");
  }
}
