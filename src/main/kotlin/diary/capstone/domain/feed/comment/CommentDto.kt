package diary.capstone.domain.feed.comment

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.NotBlank

data class CommentRequestForm(
    @field:NotBlank
    var content: String
)

data class CommentResponse(
    var id: Long?,
    var writer: UserSimpleResponse,
    var content: String,
    var childCount: Int,
    var layer: Int,
    var parentId: Long,
    var likeCount: Int,
    var isLiked: Boolean,
    var isFollowed: Boolean,
    var createTime: String,
) {
    constructor(comment: Comment, user: User): this(
        id = comment.id,
        writer = UserSimpleResponse(comment.writer, user),
        content = comment.content,
        childCount = comment.children.size,
        layer = comment.layer,
        parentId = comment.parent?.let { it.id } ?: 0L,
        likeCount = comment.likes
            .count { it.comment.id == comment.id },
        isLiked = comment.likes
            .any { it.comment.id == comment.id && it.user.id == user.id },
        isFollowed = user.following // 조회하는 유저의 피드 작성자 팔로우 유무
            .any { it.target.id == comment.writer.id },
        createTime = comment.createTime,
    )
}

data class CommentPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var comments: List<CommentResponse>
) {
    constructor(comments: Page<Comment>, user: User): this(
        currentPage = comments.number + 1,
        totalPages = comments.totalPages,
        totalElements = comments.totalElements,
        comments = comments.content
            .map { CommentResponse(it, user) }
    )
    constructor(comments: Page<CommentResponse>): this(
        currentPage = comments.number + 1,
        totalPages = comments.totalPages,
        totalElements = comments.totalElements,
        comments = comments.content
    )
}