package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
import diary.capstone.util.getPagedObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChatService(
    private val chatSessionRepository: ChatSessionRepository,
    private val userService: UserService
) {

    fun createChatSession(targetUserId: Long, loginUser: User): ChatSession {
        val targetUser = userService.getUser(targetUserId)
        val targetChatSessions = targetUser.chatSession.map { it.chatSession.id }
        val userChatSessions = loginUser.chatSession.map { it.chatSession.id }
        val union = targetChatSessions + userChatSessions
        val intersection = union.groupBy { it }.filter { it.value.size > 1 }.flatMap { it.value }.distinct()

        return if (intersection.isNotEmpty()) getChatSession(intersection[0]!!)
        else chatSessionRepository
            .save(ChatSession(name = "${targetUser.name}/${loginUser.name}"))
            .setUsers(listOf(targetUser, loginUser))
    }

    fun createChat(chatSessionId: Long, chatMessage: ChatMessage) =
        getChatSession(chatSessionId).let { chatSession ->
            chatSession.chats.add(
                Chat(
                    sender = userService.getUser(chatMessage.sender),
                    message = chatMessage.message,
                    chatSession = chatSession
                )
            )
        }

    @Transactional(readOnly = true)
    fun getChatSession(chatSessionId: Long): ChatSession =
        chatSessionRepository.findById(chatSessionId).orElseThrow { throw ChatException(CHAT_SESSION_NOT_FOUND) }

    @Transactional(readOnly = true)
    fun getAllChatSession(loginUser: User): List<ChatSession> =
        loginUser.chatSession.map { it.chatSession }

    @Transactional(readOnly = true)
    fun getChatLog(pageable: Pageable, chatSessionId: Long, loginUser: User): Page<Chat> =
        getPagedObject(pageable,
            getChatSession(chatSessionId).chats
        )

    fun deleteChatSession(chatSessionId: Long) =
        chatSessionRepository.deleteById(chatSessionId)
}