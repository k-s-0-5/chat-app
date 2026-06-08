package com.webapp.example;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.example.account.AccountRepository;
import com.webapp.example.account.AccountService;
import com.webapp.example.account.Accounts;
import com.webapp.example.conversation.ConversationRepository;
import com.webapp.example.conversation.Conversations;
import com.webapp.example.message.MessageRepository;
import com.webapp.example.message.Messages;
import com.webapp.example.participant.ParticipantRepository;
import com.webapp.example.participant.Participants;

@Component
public class DataLoader implements CommandLineRunner {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ParticipantRepository participantRepository;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;

    public DataLoader(MessageRepository messageRepository,
            AccountRepository accountRepository,
            ParticipantRepository participantRepository,
            ConversationRepository conversationRepository,
            ObjectMapper objectMapper, AccountService accountService) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.participantRepository = participantRepository;
        this.conversationRepository = conversationRepository;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (conversationRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/conversations.json")) {
                Conversations allConversations = objectMapper.readValue(inputStream, Conversations.class);
                conversationRepository.saveAll(allConversations.conversations());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {

        }

        if (accountRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/accounts.json")) {
                Accounts allAccounts = objectMapper.readValue(inputStream, Accounts.class);
                accountService.saveAll(allAccounts.accounts());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {

        }

        if (participantRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/participants.json")) {
                Participants allParticipants = objectMapper.readValue(inputStream, Participants.class);
                participantRepository.saveAll(allParticipants.participants());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {

        }

        if (messageRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/messages.json")) {
                Messages allMessages = objectMapper.readValue(inputStream, Messages.class);
                messageRepository.saveAll(allMessages.messages());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {

        }
    }
}