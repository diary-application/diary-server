package diary.capstone.domain.chat

import diary.capstone.auth.Auth
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
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
import org.springframework.transaction.annotation.Transactional
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
    ) = ChatLogPagedResponse(chatService.getChatLog(pageable, chatSessionId, user), user)

    @PostMapping("/session/{chatSessionId}/chat/{chatId}")
    fun readChat(
        @PathVariable("chatSessionId") chatSessionId: Long,
        @PathVariable("chatId") chatId: Long,
        @ApiIgnore user: User
    ) = chatService.readChat(chatSessionId, chatId, user)

    @DeleteMapping("/session/{chatSessionId}")
    fun deleteChatSession(@PathVariable("chatSessionId") chatSessionId: Long, @ApiIgnore user: User) =
        chatService.deleteChatSession(chatSessionId, user)
}

// 채팅 메시지 전송 핸들러
@RestController
class ChatMessageController(
    private val sendingOperations: SimpMessageSendingOperations,
    private val chatService: ChatService,
    private val userService: UserService
) {

    /**
     * 채팅 메시지 전송을 위한 핸들러 메소드
     * - topic: /chat/{chatSessionId}
     * - MessageMapping: 해당 topic 으로 메시지를 전송, 클라이언트는 요청 시 /pub 을 prefix 로 요청한다
     * - SendTo: 퍼블리싱 된 메시지를 해당 topic 을 구독한 사용자들에게 전송한다.
     * - topic 구독: 클라이언트는 /sub 을 prefix 로 요청하여 해당 topic 을 구독한다.
     */
    @Transactional
    @MessageMapping("/chat/{chatSessionId}")
    @SendTo("/sub/chat/{chatSessionId}")
    fun sendMessage(
        @DestinationVariable("chatSessionId") chatSessionId: Long,
        @Payload chatRequest: ChatRequest,
        accessor: SimpMessageHeaderAccessor
    ): ChatResponse {
        logger().info("{}: {}", chatRequest.sender, chatRequest.message)
        val chat = ChatResponse(
            chatService.createChat(chatSessionId, chatRequest),
            userService.getUser(chatRequest.sender)
        )
        sendingOperations.convertAndSend("/pub/chat/${chatSessionId}", chat)
        return chat
    }
}