package diary.capstone.domain.feed

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class FeedRequestForm(
    @field:NotBlank
    var content: String,

    @field:Pattern(regexp = "^(all|followers|me)$", message = "all, followers, me 로만 입력 가능합니다.")
    var showScope: String
)

data class FeedSimpleResponse(
    var id: Long,
    var writer: UserSimpleResponse,
    var content: String,
    var commentCount: Int,
    var likeCount: Int,
    var isLiked: Boolean,
    var isFollowed: Boolean,
    var createTime: String
) {
    constructor(feed: Feed, user: User): this(
        id = feed.id!!,
        writer = UserSimpleResponse(feed.writer),
        content = feed.content,
        commentCount = feed.comments.size,
        likeCount = feed.likes
            .count { it.feed.id == feed.id },
        isLiked = feed.likes // 조회하는 유저의 해당 피드 좋아요 유무
            .any { it.feed.id == feed.id && it.user.id == user.id },
        isFollowed = user.following // 조회하는 유저의 피드 작성자 팔로우 유무
            .any { it.target.id == feed.writer.id },
        createTime = feed.createTime
    )
}

// 피드 상세 보기, 댓글 데이터는 해당 API를 통해 따로 요청
data class FeedDetailResponse(
    var id: Long,
    var writer: UserSimpleResponse,
    var content: String,
    var commentCount: Int,
//    var comments: List<CommentResponse>,
    var likeCount: Int,
    var isLiked: Boolean,
    var isFollowed: Boolean,
    var createTime: String
) {
    constructor(feed: Feed, user: User): this(
        id = feed.id!!,
        writer = UserSimpleResponse(feed.writer),
        content = feed.content,
        commentCount = feed.comments.size,
//        comments = feed.comments
//            .filter { it.parent == null }
//            .map { CommentResponse(it) },
        likeCount = feed.likes.size,
        isLiked = feed.likes
            .any { it.user.id == user.id },
        isFollowed = user.following
            .any { it.target.id == feed.writer.id },
        createTime = feed.createTime,
    )
}

data class FeedPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var feeds: List<FeedSimpleResponse>
) {
    constructor(feeds: Page<Feed>, user: User): this(
        currentPage = feeds.number + 1,
        totalPages = feeds.totalPages,
        totalElements = feeds.totalElements,
        feeds = feeds.content
            .map { FeedSimpleResponse(it, user) }
            .toList()
    )
}