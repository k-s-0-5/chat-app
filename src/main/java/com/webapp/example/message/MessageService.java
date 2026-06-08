package com.webapp.example.message;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    MessageService (MessageRepository messageRepository){ 
        this.messageRepository = messageRepository;
    }

    public List<Message> findByConversationId(Integer conversationId) 
    {
        return messageRepository.findByConversationId(conversationId);
    }

}
