package com.webapp.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// https://www.youtube.com/watch?v=XY5CUuE6VOk
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

  private final MessageInterceptor interceptor;

  WebsocketConfig(MessageInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/ws");
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/connect");
  }

  // https://medium.com/@abasjama04/implementing-websockets-in-spring-boot-and-angular-for-real-time-bidirectional-communication-c3307d046dff
  // @ConfigureInboundChannel allows us to register custom interceptors that intercept STOMP
  // commands and access any credentials passed in the headers
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(interceptor);
  }
}
