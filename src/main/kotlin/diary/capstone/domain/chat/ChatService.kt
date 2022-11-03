package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
import diary.capstone.util.getPagedObject
import diary.capstone.util.logger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class ChatService(
    private val chatSessionRepository: ChatSessionRepository,
    private val chatRepository: ChatRepository,
    private val userService: UserService
) {

    /**
     * 채팅 세션 생성
     * - 본인과 채팅할 유저의 id를 요청
     * - 본인과 채팅할 유저의 채팅 세션이 이미 있다면 해당 채팅 세션 반환
     * - 없을 경우 새 채팅 세션을 만들어 반환
     */
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

    // 해당 채팅 세션에 채팅 저장
    fun createChat(chatRequest: ChatRequest) =
        getChatSession(chatRequest.sessionId).let { chatSession ->
            chatRepository.save(
                Chat(
                    sender = userService.getUser(chatRequest.sender),
                    message = chatRequest.message,
                    chatSession = chatSession
                )
            ).let {
                chatSession.chats.add(it)
                it.addReadUser(chatRequest.sender)
                it
            }
        }

    // 해당 채팅 세션 조회
    @Transactional(readOnly = true)
    fun getChatSession(chatSessionId: Long): ChatSession =
        chatSessionRepository.findById(chatSessionId)
            .orElseThrow { throw ChatException(CHAT_SESSION_NOT_FOUND) }

    // 해당 유저의 모든 채팅 세션 조회
    @Transactional(readOnly = true)
    fun getAllChatSession(loginUser: User): List<ChatSession> =
        loginUser.chatSession.map { it.chatSession }

    // 해당 채팅 세션의 채팅 목록 조회 + 안읽은 채팅 모두 읽기
    fun getChatLog(pageable: Pageable, chatSessionId: Long, loginUser: User): Page<Chat> =
        getChatSession(chatSessionId).let { chatSession ->
            chatSession.chats
                .filter { !it.getReadUsers().contains(loginUser.id) }
                .forEach { it.addReadUser(loginUser.id!!) }
            getPagedObject(pageable, chatSession.chats.sortedByDescending { it.id })
        }

    // 채팅 세션 삭제(채팅 세션에 포함된 유저만 삭제 가능)
    fun deleteChatSession(chatSessionId: Long, loginUser: User) =
        getChatSession(chatSessionId).let { chatSession ->
            if (chatSession.sessionUsers.any { it.user.id == loginUser.id })
                chatSessionRepository.delete(chatSession)
            else throw ChatException(CHAT_SESSION_ACCESS_DENIED)
        }
}