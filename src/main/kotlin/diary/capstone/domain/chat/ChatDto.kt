package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse

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