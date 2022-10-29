package diary.capstone.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig: WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/ws/chat")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // /sub 가 prefix 로 붙은 destination 의 클라이언트에게 메시지를 전송
        registry.enableSimpleBroker("/sub")

        // /pub 가 prefix 로 붙은 메시지들은 @MessageMapping 이 붙은 메소드로 바운드됨
        registry.setApplicationDestinationPrefixes("/pub")
    }
}