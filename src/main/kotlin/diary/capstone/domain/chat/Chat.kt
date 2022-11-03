package diary.capstone.domain.chat

import diary.capstone.domain.user.User
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

@Entity
class ChatSession(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_session_id")
    var id: Long? = null,

    var name: String = "",
    
    @OneToMany(mappedBy = "chatSession", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sessionUsers: MutableList<ChatSessionUser> = mutableListOf(),

    @OneToMany(mappedBy = "chatSession", cascade = [CascadeType.ALL], orphanRemoval = true)
    var chats: MutableList<Chat> = mutableListOf(),
) {
    fun setUsers(users: List<User>): ChatSession {
        users.forEach {
            this.sessionUsers.add(ChatSessionUser(user = it, chatSession = this))
        }
        return this
    }

    fun getUnreadChats(user: User): List<Chat> =
        this.chats.filter { !it.getReadUsers().contains(user.id) }
}

// 유저의 채팅 세션 구독 정보
@Entity
class ChatSessionUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_session_user_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id")
    var chatSession: ChatSession
)

@Entity
class Chat(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    var id: Long? = null,

    var message: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id")
    var chatSession: ChatSession,

    var readUsers: String = "",

): BaseTimeEntity() {

    fun getReadUsers(): List<Long> {
        return if (this.readUsers == "") listOf()
        else this.readUsers.split(",").map { it.toLong() }
    }

    fun addReadUser(userId: Long) {
        if (this.readUsers == "") this.readUsers = userId.toString()
        else this.readUsers += ",${userId}"
    }
}