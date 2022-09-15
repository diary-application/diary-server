package diary.capstone.domain.feed

// 피드
const val FEED_NOT_FOUND = "해당 글을 찾을 수 없습니다."
const val FEED_ACCESS_DENIED = "해당 글에 대한 접근 권한이 없습니다."

// 좋아요
const val ALREADY_LIKED_FEED = "이미 좋아요를 누른 피드입니다."

// 댓글
const val COMMENT_NOT_FOUND = "해당 댓글을 찾을 수 없습니다."
const val COMMENT_ACCESS_DENIED = "해당 댓글에 대한 접근 권한이 없습니다."

class FeedException(message: String): RuntimeException(message)
class FeedLikeException(message: String): RuntimeException(message)
class CommentException(message: String): RuntimeException(message)