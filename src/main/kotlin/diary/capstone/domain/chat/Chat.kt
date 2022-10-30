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
            this.sessionUsers.add(
                ChatSessionUser(user = it, chatSession = this)
            )
        }
        return this
    }
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

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.ALL], orphanRemoval = true)
    var chatReadUser: MutableList<ChatReadUser> = mutableListOf(),

    var createTime: String
)

@Entity
class ChatReadUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_read_user_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    var chat: Chat,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,
)