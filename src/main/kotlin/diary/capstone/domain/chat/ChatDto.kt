package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import org.springframework.data.domain.Page

// Stomp payload 로 전송되는 채팅 메시지 스펙
data class ChatRequest(
    var sessionId: Long,
    var sender: Long,
    var message: String
)

data class ChatResponse(
    var id: Long,
    var sessionId: Long,
    var sender: UserSimpleResponse,
    var message: String,
    var createTime: String,
    var readUsers: List<Long>
) {
    constructor(chat: Chat, user: User): this(
        id = chat.id ?: 0,
        sessionId = chat.chatSession.id!!,
        sender = UserSimpleResponse(chat.sender, user),
        message = chat.message,
        createTime = chat.createTime,
        readUsers = chat.getReadUsers()
    )
}

data class ChatSessionCreateForm(var targetUserId: Long)

data class ChatSessionResponse(
    var id: Long,
    var users: List<UserSimpleResponse>,
    var lastChat: String?,
    var unreadCount: Int,
) {
    constructor(chatSession: ChatSession, user: User): this(
        id = chatSession.id!!,
        users = chatSession.sessionUsers
            .filter { it.user.id != user.id }
            .map { UserSimpleResponse(it.user, user) },
        lastChat = chatSession.chats
            .let { chats ->
                if (chats.isEmpty()) null
                else chats.last().message
            },
        unreadCount = chatSession.getUnreadChats(user).size
    )
}

data class ChatPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var chats: List<ChatResponse>
) {
    constructor(chats: Page<Chat>, user: User): this(
        currentPage = chats.number + 1,
        totalPages = chats.totalPages,
        chats = chats.content
            .reversed()
            .map { ChatResponse(it, user) }
    )
}