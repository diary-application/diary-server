package diary.capstone.domain.notice

import diary.capstone.domain.user.User
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

@Entity
class Notice(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    var receiver: User,

    var type: String,
    var content: String

): BaseTimeEntity() {
    fun getTypeAndId(): Pair<String, Long> =
        this.type.split("/").let {
            Pair(it[0], it[1].toLong())
        }
}