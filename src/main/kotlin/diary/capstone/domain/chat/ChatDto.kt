package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import org.springframework.data.domain.Page

// Stomp payload 로 전송되는 채팅 메시지 스펙
data class ChatRequest(
    var sender: Long,
    var message: String
)

data class ChatResponse(
    var sender: UserSimpleResponse,
    var message: String,
    var createTime: String,
) {
    constructor(chat: Chat, user: User): this(
        sender = UserSimpleResponse(chat.sender, user),
        message = chat.message,
        createTime = chat.createTime
    )
}

data class ChatSessionCreateForm(var targetUserId: Long)

data class ChatSessionResponse(
    var id: Long,
    var users: List<UserSimpleResponse>,
    var lastChat: ChatResponse,
    var hasReadLastChat: Boolean
) {
    constructor(chatSession: ChatSession, user: User): this(
        id = chatSession.id!!,
        users = chatSession.sessionUsers
            .filter { it.user.id != user.id }
            .map { UserSimpleResponse(it.user, user) },
        lastChat = ChatResponse(chatSession.chats.last(), user),
        hasReadLastChat = chatSession.chats.last().chatReadUser
            .any { it.user.id == user.id }
    )
}

data class ChatLogPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var chats: List<ChatResponse>
) {
    constructor(chats: Page<Chat>, user: User): this(
        currentPage = chats.number + 1,
        totalPages = chats.totalPages,
        totalElements = chats.totalElements,
        chats = chats.content
            .map { ChatResponse(it, user) }
            .toList()
    )
}