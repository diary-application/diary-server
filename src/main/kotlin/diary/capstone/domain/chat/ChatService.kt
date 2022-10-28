package diary.capstone.domain.chat

import org.springframework.stereotype.Service

@Service
class ChatService {

    private val rooms: MutableList<ChatRoom> = mutableListOf()

    fun getAllRooms(): List<ChatRoom> = rooms

    fun getRoom(roomId: Long): ChatRoom =
        rooms.find { it.roomId == roomId } ?: throw RuntimeException("해당 채팅방이 없습니다.")

    fun createRoom(roomName: String): ChatRoom =
        ChatRoom(roomId = rooms.size.toLong(), roomName = roomName).let {
            rooms.add(it)
            it
        }
}