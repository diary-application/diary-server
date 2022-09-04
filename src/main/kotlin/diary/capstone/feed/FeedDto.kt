package diary.capstone.feed

import diary.capstone.user.User
import diary.capstone.user.UserSimpleResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.NotBlank

data class FeedRequestForm(
    @field:NotBlank
    var content: String,
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
        isLiked = feed.likes
            .any { it.feed.id == feed.id && it.user.id == user.id },
        isFollowed = user.following
            .any { it.user.id == user.id && it.followUser.id == feed.writer.id },
        createTime = feed.createTime
    )
}

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
        likeCount = feed.likes
            .count { it.feed.id == feed.id },
        isLiked = feed.likes
            .any { it.feed.id == feed.id && it.user.id == user.id },
        isFollowed = user.following
            .any { it.user.id == user.id && it.followUser.id == feed.writer.id },
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