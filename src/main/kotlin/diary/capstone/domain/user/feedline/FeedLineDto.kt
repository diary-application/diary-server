package diary.capstone.domain.user.feedline

class FeedLineRequestForm(
    var title: String,
)

data class FeedLineResponse(var id: Long, var title: String) {
    constructor(feedLine: FeedLine): this(
        id = feedLine.id!!,
        title = feedLine.title
    )
}