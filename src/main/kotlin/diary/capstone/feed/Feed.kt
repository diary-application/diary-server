package diary.capstone.feed

import diary.capstone.user.User
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

// 공개 범위 const
const val SHOW_ALL = "all"
const val SHOW_FOLLOWERS = "follower"
const val SHOW_ME = "me"

@Entity
class Feed(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var writer: User,

    var content: String,

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true)
    var likes: MutableList<FeedLike> = mutableListOf(),

    // 피드 공개 범위
    var showScope: String

): BaseTimeEntity() {
    fun update(
        content: String? = null,
        showScope: String? = null
    ) {
        content?.let { this.content = content }
        showScope?.let { this.showScope = showScope }
    }
}

@Entity
class FeedLike(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    var feed: Feed, // 좋아요 한 피드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User, // 좋아요 누른 유저
)