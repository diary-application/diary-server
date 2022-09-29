package diary.capstone.domain.feed.comment

import diary.capstone.domain.feed.Feed
import diary.capstone.domain.user.User
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

@Entity
class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    var feed: Feed,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var writer: User,

    var content: String,

    // 계층형 댓글 구조
    var layer: Int = 1, // 계층

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    var children: MutableList<Comment> = mutableListOf(),

    ): BaseTimeEntity() {
    fun update(content: String): Comment {
        this.content = content
        return this
    }
}