package diary.capstone.domain.feed

// 피드
const val FEED_NOT_FOUND = "해당 글을 찾을 수 없습니다."
const val FEED_ACCESS_DENIED = "해당 글에 대한 접근 권한이 없습니다."
const val INVALID_FEED_FORM = "클라이언트 : 입력 폼이 잘못되었습니다."

// 피드 라인
const val FEED_LINE_NOT_FOUND = "해당 피드 라인을 찾을 수 없습니다."

// 좋아요
const val ALREADY_LIKED_FEED = "이미 좋아요를 누른 피드입니다."
const val ALREADY_LIKED_COMMENT = "이미 좋아요를 누른 댓글입니다."

// 저장
const val ALREADY_SAVED_FEED = "이미 저장한 피드입니다."

// 댓글
const val COMMENT_NOT_FOUND = "해당 댓글을 찾을 수 없습니다."
const val COMMENT_ACCESS_DENIED = "해당 댓글에 대한 접근 권한이 없습니다."

class FeedException(message: String): RuntimeException(message)
class FeedLikeException(message: String): RuntimeException(message)
class FeedSaveException(message: String): RuntimeException(message)
class CommentException(message: String): RuntimeException(message)
class CommentLikeException(message: String): RuntimeException(message)