package com.webapp.example.Errors;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ProblemDetail> accountNotFound(
      AccountNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Account not found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ProblemDetail> messageNotFound(
      MessageNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Message not found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @ExceptionHandler(ConversationNotFoundException.class)
  public ResponseEntity<ProblemDetail> conversationNotFound(
      ConversationNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Conversation not found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @ExceptionHandler(IllegalArgumentException.class) // Thrown at wrong shape for db
  public ResponseEntity<ProblemDetail> argumentIllegal(IllegalArgumentException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Argument not legal");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class) // Thrown at @valid
  public ResponseEntity<ProblemDetail> argumentInvalid(MethodArgumentNotValidException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Invalid argument");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @MessageExceptionHandler(CryptoException.class)
  public String cryptoError(CryptoException ex) {
    return ex.getMessage();
  }
}
