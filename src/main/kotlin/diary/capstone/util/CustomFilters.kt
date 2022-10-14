package diary.capstone.util

import diary.capstone.domain.feed.Feed
import diary.capstone.domain.feed.SHOW_FOLLOWERS
import diary.capstone.domain.feed.SHOW_ME
import diary.capstone.domain.feed.comment.Comment

/**
 * 루트 댓글 필터 (부모 댓글이 없는 경우)
 */
fun Iterable<Comment>.filterRootComments(): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.parent == null }

/**
 * 대댓글 필터 (부모 댓글의 id가 commentId인 댓글)
 */
fun Iterable<Comment>.filterChildComments(commentId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.parent?.id == commentId }

/**
 * 특정 유저가 쓴 댓글 필터 (작성자의 id가 userId인 댓글)
 */
fun Iterable<Comment>.filterSpecificUserComments(userId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.writer.id == userId }

/**
 * 특정 유저가 쓴 댓글 제외 (작성자의 id가 특정 유저 id가 아닌 댓글)
 */
fun Iterable<Comment>.filterNotSpecificUserComments(userId: Long): List<Comment> =
    filterTo(ArrayList()) { c: Comment -> c.writer.id != userId }

/**
 * 공개 범위가 팔로워 공개인 피드 제외 (condition이 true일 경우)
 */
fun Iterable<Feed>.filterNotShowFollowersFeed(condition: Boolean): List<Feed> =
    if (condition) filterNot { f: Feed -> f.showScope == SHOW_FOLLOWERS } else this as List<Feed>

/**
 * 공개 범위가 나만 공개인 피드 제외
 */
fun Iterable<Feed>.filterNotShowMeFeed(): List<Feed> =
    filterNot { f: Feed -> f.showScope == SHOW_ME }

/**
 * 특정 유저가 쓴 피드 제외 (작성자 id가 특정 유저 id가 아닌 피드)
 */
fun Iterable<Feed>.filterNotSpecificUserFeed(userId: Long): List<Feed> =
    filterNotTo(ArrayList()) { f: Feed -> f.writer.id == userId }