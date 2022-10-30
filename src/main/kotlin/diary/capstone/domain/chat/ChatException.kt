package diary.capstone.domain.chat

const val CHAT_SESSION_NOT_FOUND = "해당 채팅 세션을 찾을 수 없습니다."

class ChatException(message: String): RuntimeException(message)