package diary.capstone.domain.chat

// Socket 을 통한 채팅 메시지 스펙
data class ChatMessage(
    var channel: Long,
    var sender: Long,
    var message: String
)

data class ChatRoom(
    var roomId: Long,
    var roomName: String
)