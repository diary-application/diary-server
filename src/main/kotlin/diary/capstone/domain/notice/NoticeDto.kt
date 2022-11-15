package diary.capstone.domain.notice

import org.springframework.data.domain.Page

data class NoticeRequest(
    var receiver: Long,
    var type: String,
    var content: String
)

data class NoticeResponse (
    var id: Long? = null,
    var receiver: Long,
    var type: String,
    var content: String,
    var createTime: String
) {
    constructor(notice: Notice): this(
        id = notice.id!!,
        receiver = notice.receiver.id!!,
        type = notice.type,
        content = notice.content,
        createTime = notice.createTime
    )

    fun getTypeAndId(): Pair<String, Long> =
        this.type.split("/").let {
            Pair(it[0], it[1].toLong())
        }
}

data class NoticePagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var notices: List<NoticeResponse>
) {
    constructor(notices: Page<Notice>): this(
        currentPage = notices.number + 1,
        totalPages = notices.totalPages,
        totalElements = notices.totalElements,
        notices = notices.content
            .map { NoticeResponse(it) }
    )
}