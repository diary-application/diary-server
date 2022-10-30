package diary.capstone.util

import diary.capstone.domain.chat.Chat
import diary.capstone.domain.feed.Feed
import diary.capstone.domain.feed.comment.Comment
import diary.capstone.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

/**
 * Pageable, 엔티티 객체의 목록을 받아
 * Paged<엔티티> 를 반환해주는 메소드
 */

fun getPagedUsers(pageable: Pageable, users: List<User>): Page<User> {
    val total = users.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(users.subList(start, end), pageable, total.toLong())
}

fun getPagedComments(pageable: Pageable, comments: List<Comment>): Page<Comment> {
    val total = comments.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(comments.subList(start, end), pageable, total.toLong())
}

fun getPagedFeed(pageable: Pageable, feeds: List<Feed>): Page<Feed> {
    val total = feeds.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(feeds.subList(start, end), pageable, total.toLong())
}

fun getPagedChat(pageable: Pageable, chats: List<Chat>): Page<Chat> {
    val total = chats.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(chats.subList(start, end), pageable, total.toLong())
}

fun <T> getPagedObject(pageable: Pageable, entityList: List<T>): Page<T> {
    val total = entityList.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(entityList.subList(start, end), pageable, total.toLong())
}