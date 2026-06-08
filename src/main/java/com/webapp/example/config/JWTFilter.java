package com.webapp.example.config;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webapp.example.auth.JWTService;
import com.webapp.example.auth.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Just extracts token data
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final MyUserDetailsService details;


    JWTFilter (JWTService jwtService, MyUserDetailsService details) {
        this.jwtService = jwtService;
        this.details = details;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;

        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("jwt_token".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null){
            username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userdetails = details.loadUserByUsername(username);
            
                if (jwtService.validateToken(token, userdetails)){
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userdetails, null, userdetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken); 
                }
            }
        }

        filterChain.doFilter(request, response);
    }

}
