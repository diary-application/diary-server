package diary.capstone.domain.chat

import org.springframework.messaging.handler.annotation.MessageMapping
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

    @MessageMapping("/chat")
    fun sendMessage(chatMessage: ChatMessage) {
        sendingOperations.convertAndSend("/pub/chat/room/${chatMessage.message}", chatMessage)
    }
}