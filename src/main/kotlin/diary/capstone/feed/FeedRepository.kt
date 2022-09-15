package diary.capstone.feed

import diary.capstone.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FeedRepository: JpaRepository<Feed, Long> {
    fun findByShowScope(pageable: Pageable, showScope: String): Page<Feed>
}

interface FeedLikeRepository: JpaRepository<FeedLike, Long> {
    fun findByFeedAndUser(feed: Feed, user: User): FeedLike?
    fun deleteByFeedAndUser(feed: Feed, user: User): Int
}