package diary.capstone.domain.chat

import diary.capstone.domain.user.UserService
import diary.capstone.util.logger
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler(private val userService: UserService): TextWebSocketHandler() {

    // 소켓 사용자 세션 목록
//    private val sessionList: MutableMap<Long, WebSocketSession> = mutableMapOf()
    private val sessionList: MutableList<WebSocketSession> = mutableListOf()

    // 클라이언트가 소켓 연결 시
    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionList.add(session)
    }

    // 클라이언트가 소켓 종료 시
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session)
    }

    // 클라이언트가 텍스트 메시지 전송 시
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        logger().info("{}'s Payload: {}", session.id, payload)

        sessionList.forEach { target ->
            target.sendMessage(message)
        }
    }
}