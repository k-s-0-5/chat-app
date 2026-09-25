package com.webapp.example;

import com.webapp.example.account.Account;
import com.webapp.example.account.AccountRepository;
import com.webapp.example.account.AccountService;
import com.webapp.example.conversation.Conversation;
import com.webapp.example.conversation.ConversationRepository;
import com.webapp.example.message.Message;
import com.webapp.example.message.MessageRepository;
import com.webapp.example.participant.Participant;
import com.webapp.example.participant.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner load(
      AccountRepository accountRepository,
      ConversationRepository conversationRepository,
      ParticipantRepository participationRepository,
      MessageRepository messageRepository,
      AccountService accountService) {

    return args -> {
      UUID acc1 = UUID.randomUUID();
      UUID acc2 = UUID.randomUUID();
      UUID acc3 = UUID.randomUUID();
      UUID con1 = UUID.randomUUID();
      accountService.testRegister(
          new Account(acc1, "user1", "user1@example.com", "1", "ROLE_USER"));
      accountService.testRegister(
          new Account(acc2, "user2", "user2@example.com", "2", "ROLE_USER"));
      accountService.testRegister(
          new Account(acc3, "user3", "user3@example.com", "3", "ROLE_USER"));
      conversationRepository.create(
          new Conversation(con1, "user1 & user2", LocalDateTime.parse("2026-04-25T10:01:00")));
      participationRepository.create(new Participant(1, acc1, con1, LocalDateTime.now(), "ADMIN"));
      participationRepository.create(new Participant(2, acc2, con1, LocalDateTime.now(), "USER"));

      String[] texts = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", 
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", 
        "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", 
        "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", 
      };

      LocalDateTime time = LocalDateTime.parse("2026-05-25T10:01:00");
      for (int i = 0; i < 50; i++) {
          UUID sender = (i % 2 == 0) ? acc1 : acc2;
          messageRepository.testCreate(new Message((long) i, sender, con1, time, texts[i], false));
          time = time.plusDays(1);
      }
    };
  }
}
