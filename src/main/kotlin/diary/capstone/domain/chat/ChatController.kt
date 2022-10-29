package diary.capstone.domain.chat

import diary.capstone.util.logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatController(private val chatService: ChatService) {

    @PostMapping("/room")
    fun createRoom(@RequestBody roomName: String): List<ChatRoom> =
        chatService.createRoom(roomName).let { chatService.getAllRooms() }

    @GetMapping("/rooms")
    fun getAllRooms(): List<ChatRoom> =
        chatService.getAllRooms()

    @GetMapping("/room/{roomId}")
    fun getRoom(@PathVariable("roomId") id: Long): ChatRoom =
        chatService.getRoom(id)
}

// 채팅 메시지 전송 핸들러
@RestController
class ChatMessageController(private val sendingOperations: SimpMessageSendingOperations) {

    @MessageMapping("/chat/0")
    @SendTo("/sub/chat/0")
    fun sendMessage(@Payload chatMessage: ChatMessage, accessor: SimpMessageHeaderAccessor): ChatMessage {
        logger().info("{}: {}", chatMessage.sender, chatMessage.message)
        sendingOperations.convertAndSend("/pub/chat/0", chatMessage)
        return chatMessage
    }
}