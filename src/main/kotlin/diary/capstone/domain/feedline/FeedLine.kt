package diary.capstone.domain.feedline

import diary.capstone.domain.user.User
import javax.persistence.*

/**
 * 정렬 기준, 필터링을 위한 엔티티
 * 한 유저 당 3개까지 작성 가능
 */
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
    var title: String = "",

    // 정렬 기준 asc/desc(날짜순, 좋아요순)
    var sortBy: String = "",

    // 선택한 카테고리 목록(,로 구분)
    var categories: String = ""
) {
    // 카테고리 문자열 파싱해서 리스트로 반환
    fun getCategories(): List<String> {
        return if (this.categories == "") listOf() else this.categories.split(",")
    }
}