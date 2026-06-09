package com.webapp.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// https://www.youtube.com/watch?v=XY5CUuE6VOk
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/ws");
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/connect");
  }
}
