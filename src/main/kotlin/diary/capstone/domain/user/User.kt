package diary.capstone.domain.user

import diary.capstone.domain.feed.Feed
import diary.capstone.domain.feed.FeedLike
import diary.capstone.domain.feedline.FeedLine
import diary.capstone.domain.file.File
import diary.capstone.domain.occupation.Occupation
import diary.capstone.domain.schedule.Schedule
import diary.capstone.util.BaseTimeEntity
import javax.persistence.*

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,

    var email: String,
    var password: String,
    var name: String,

    // 최근 접속 ip, 로그인 대기 상태
    var ip: String = "",
    var loginWait: Boolean = false,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "occupation_id")
    var occupation: Occupation? = null, // 직종
    var interests: String = "", // 관심 분야(직종 이름들): ,로 구분하여 3개까지

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "file_id")
    var profileImage: File? = null,

    @OneToMany(mappedBy = "writer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var feeds: MutableList<Feed> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var feedLikes: MutableList<FeedLike> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var feedLines: MutableList<FeedLine> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var following: MutableList<Follow> = mutableListOf(),

    @OneToMany(mappedBy = "target", cascade = [CascadeType.ALL], orphanRemoval = true)
    var follower: MutableList<Follow> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var schedules: MutableList<Schedule> = mutableListOf()

): BaseTimeEntity() {
    fun update(
        password: String? = null,
        name: String? = null,
        email: String? = null,
        ip: String? = null,
        loginWaiting: Boolean? = null,
        occupation: Occupation? = null,
        interests: String? = null,
        profileImage: File? = null
    ): User {
        password?.let { this.password = password }
        name?.let { this.name = name }
        email?.let { this.email = email }
        ip?.let { this.ip = ip }
        loginWaiting?.let { this.loginWait = loginWaiting }
        occupation?.let { this.occupation = occupation }
        interests?.let { this.interests = interests }
        profileImage?.let { this.profileImage = profileImage }
        return this
    }

    /**
     * interests 프로퍼티는 String 타입으로 여러 직종을 ,로 구분하여 저장.
     * 해당 프로퍼티를 조회할 땐 아래 메소드를 사용하여 파싱 후 조회해야함
     */
    fun getInterests(): List<String> {
        return if (this.interests == "") listOf() else this.interests.split(",")
    }

    fun addSchedule(schedule: Schedule) {
        this.schedules.add(schedule)
    }
}

@Entity
class Follow(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    var id: Long? = null,

    // 팔로우 한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    // 팔로우 대상 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    var target: User,
)