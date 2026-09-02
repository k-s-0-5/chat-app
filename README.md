# Chat-App
A local real-time chat application. The application uses a Spring Boot backend with WebSocket messaging and stateless JWT authentication; Rest APIs and H2 for persistence. 

---

## About 
<p align="center"><img width="522" height="522" alt="Class" src="https://github.com/user-attachments/assets/7dab590a-1932-4518-8013-26abbb7731ef" /></p>

The diagram models the relation between the four primary entities of the application: Account, Message, Participant, and Conversation. Each account can send many messages, and participate in many conversations. Participant is a junction class which holds information about the Accounts role within a Conversation.



<p align="center"><img width="522" height="382" alt="Sequence" src="https://github.com/user-attachments/assets/de6e39f5-6da3-496c-b10f-02d3fe0fe9ff" /></p>
The diagram depicts a user sending a message. First the user connects to the Stomp client and subscribes to a conversation. The user then sends the contents of a message. The server persists a Message object and then distributes it to subscribers. The message is then rendered for each subscriber.
