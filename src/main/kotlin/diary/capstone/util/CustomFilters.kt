package diary.capstone.util

import diary.capstone.feed.Comment

/**
 * 루트 댓글 목록 조회 (부모 댓글이 없는 경우)
 */
fun Iterable<Comment>.filterRootComments(): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.parent == null }

/**
 * 대댓글 목록 조회 (부모 댓글의 id가 commentId인 댓글)
 */
fun Iterable<Comment>.filterChildComments(commentId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.parent?.id == commentId }

/**
 * 내가 쓴 댓글 목록 조회 (작성자의 id가 userId인 댓글)
 */
fun Iterable<Comment>.filterMyComments(userId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.writer.id == userId }

/**
 * 내가 쓴 댓글 외 모든 댓글 목록 조회 (작성자의 id가 userId가 아닌 댓글)
 */
fun Iterable<Comment>.filterNotMyComments(userId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.writer.id != userId }

// 동적 조건으로 데이터 조회
//    private fun Iterable<Comment>.filterMyComments(condition: Boolean, userId: Long): List<Comment> =
//        if (condition) filterTo(ArrayList()) { c: Comment -> c.writer.id == userId } else this as List<Comment>