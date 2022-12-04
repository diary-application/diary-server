package diary.capstone.domain.feed.comment

import diary.capstone.config.COMMENT_PAGE_SIZE
import diary.capstone.domain.feed.Feed
import diary.capstone.domain.user.User
import diary.capstone.util.BaseTimeEntity
import org.hibernate.annotations.BatchSize
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
    var layer: Int = 1, // 계층

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @BatchSize(size = COMMENT_PAGE_SIZE)
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<Comment> = mutableListOf(),

    @BatchSize(size = COMMENT_PAGE_SIZE)
    @OneToMany(mappedBy = "comment", cascade = [CascadeType.ALL], orphanRemoval = true)
    var likes: MutableList<CommentLike> = mutableListOf(),

): BaseTimeEntity() {
    fun update(content: String): Comment {
        this.content = content
        return this
    }
}

@Entity
class CommentLike(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    var comment: Comment,
)