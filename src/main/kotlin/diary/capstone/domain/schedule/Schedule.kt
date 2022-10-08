package diary.capstone.domain.schedule

import diary.capstone.domain.user.User
import javax.persistence.*

@Entity
class Schedule (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    var date: String,
    var content: String,
) {
    fun update(
        date: String? = null,
        content: String? = null
    ) {
        date?.let { this.date = date }
        content?.let { this.content = content }
    }
}