package diary.capstone.domain.user

import diary.capstone.domain.feed.Feed
import diary.capstone.domain.feed.feedline.FeedLine
import diary.capstone.domain.file.File
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,

    var uid: String,
    var password: String,
    var name: String,
    var email: String,
    var job: String = "",
    var category: String = "",

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "file_id")
    var profileImage: File? = null,

    @OneToMany(mappedBy = "writer", cascade = [CascadeType.ALL])
    var feeds: MutableList<Feed> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var feedLines: MutableList<FeedLine> = mutableListOf(),

    @OneToMany(mappedBy = "causer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var following: MutableList<Follow> = mutableListOf(),

    @OneToMany(mappedBy = "target", cascade = [CascadeType.ALL], orphanRemoval = true)
    var follower: MutableList<Follow> = mutableListOf()

): BaseTimeEntity() {
    fun update(
        password: String? = null,
        name: String? = null,
        email: String? = null,
        job: String? = null,
        category: String? = null,
        profileImage: File? = null
    ): User {
        password?.let { this.password = password }
        name?.let { this.name = name }
        email?.let { this.email = email }
        job?.let { this.job = job }
        category?.let { this.category = category }
        profileImage?.let { this.profileImage = profileImage }
        return this
    }
}

@Entity
class Follow(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // 팔로우 한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var causer: User,

    // 팔로우 대상 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    var target: User,
)