package diary.capstone.domain.chat

import diary.capstone.auth.Auth
import diary.capstone.domain.user.User
import diary.capstone.util.logger
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@Auth
@RestController
@RequestMapping("/chat")
class ChatSessionController(private val chatService: ChatService) {

    @PostMapping("/session")
    fun createChatSession(
        @RequestBody chatSessionCreateForm: ChatSessionCreateForm,
        @ApiIgnore user: User
    ) = ChatSessionResponse(chatService.createChatSession(chatSessionCreateForm.targetUserId, user), user)

    @GetMapping("/session")
    fun getAllChatSession(@ApiIgnore user: User) =
        chatService.getAllChatSession(user).map { ChatSessionResponse(it, user) }

    @GetMapping("/session/{chatSessionId}")
    fun getChatLog(
        @PageableDefault(size = 50, sort = ["chat_id"], direction = Sort.Direction.DESC) pageable: Pageable,
        @PathVariable("chatSessionId") chatSessionId: Long,
        @ApiIgnore user: User
    ) = ChatLogPagedResponse(chatService.getChatLog(pageable, chatSessionId, user))
}

// 채팅 메시지 전송 핸들러
@RestController
class ChatMessageController(
    private val sendingOperations: SimpMessageSendingOperations,
    private val chatService: ChatService
) {

    @MessageMapping("/chat/{chatSessionId}")
    @SendTo("/sub/chat/{chatSessionId}")
    fun sendMessage(
        @DestinationVariable("chatSessionId") chatSessionId: Long,
        @Payload chatMessage: ChatMessage,
        accessor: SimpMessageHeaderAccessor
    ): ChatMessage {
        logger().info("{}: {}", chatMessage.sender, chatMessage.message)
        chatService.createChat(chatSessionId, chatMessage)
        sendingOperations.convertAndSend("/pub/chat/${chatSessionId}", chatMessage)
        return chatMessage
    }
}