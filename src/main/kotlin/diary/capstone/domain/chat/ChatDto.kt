package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import org.springframework.data.domain.Page

// Stomp payload 로 전송되는 채팅 메시지 스펙
data class ChatMessage(
    var sender: Long,
    var message: String
)

data class ChatSessionCreateForm(var targetUserId: Long)

data class ChatSessionResponse(
    var id: Long,
    var users: List<UserSimpleResponse>
) {
    constructor(chatSession: ChatSession, user: User): this(
        id = chatSession.id!!,
        users = chatSession.sessionUsers
            .filter { it.user.id != user.id }
            .map { UserSimpleResponse(it.user, user) }
    )
}

data class ChatLogPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var chats: List<ChatMessage>
) {
    constructor(chats: Page<Chat>): this(
        currentPage = chats.number + 1,
        totalPages = chats.totalPages,
        totalElements = chats.totalElements,
        chats = chats.content
            .map { ChatMessage(it.sender.id!!, it.message) }
            .toList()
    )
}