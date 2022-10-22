package diary.capstone.domain.feed.comment

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
    var createTime: String,
    var layer: Int
) {
    constructor(comment: Comment): this(
        id = comment.id,
        writer = UserSimpleResponse(comment.writer),
        content = comment.content,
        childCount = comment.children.size,
        createTime = comment.createTime,
        layer = comment.layer
    )
}

data class CommentPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var comments: List<CommentResponse>
) {
    constructor(comments: Page<Comment>): this(
        currentPage = comments.number + 1,
        totalPages = comments.totalPages,
        totalElements = comments.totalElements,
        comments = comments.content
            .map { CommentResponse(it) }
            .toList()
    )
}