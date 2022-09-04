package diary.capstone.feed

import diary.capstone.user.User
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

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

    // TODO 공개 범위에 따른 피드 노출 설정
    var showScope: String = "all"

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
    var feed: Feed,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,
)