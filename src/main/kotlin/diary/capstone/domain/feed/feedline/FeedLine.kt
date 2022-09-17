package diary.capstone.domain.feed.feedline

import diary.capstone.domain.user.User
import javax.persistence.*

@Entity
class FeedLine (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_line_id")
    var id: Long? = null,

    // 피드라인 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    // 피드라인 제목
    var title: String,

    // 정렬 기준(날짜순, 좋아요순)
    var sortBy: String = "",

    // 선택한 카테고리 목록
    var categories: String
)