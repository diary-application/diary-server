package diary.capstone.domain.chat

const val CHAT_SESSION_NOT_FOUND = "해당 채팅 세션을 찾을 수 없습니다."
const val CHAT_NOT_FOUND = "해당 채팅을 찾을 수 없습니다."
const val CHAT_SESSION_ACCESS_DENIED = "해당 채팅 세션에 접근할 권한이 없습니다."

class ChatException(message: String): RuntimeException(message)