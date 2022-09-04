package diary.capstone.feed

import diary.capstone.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface FeedRepository: JpaRepository<Feed, Long> {}

interface FeedLikeRepository: JpaRepository<FeedLike, Long> {
    fun findByFeedAndUser(feed: Feed, user: User): FeedLike?
    fun deleteByFeedAndUser(feed: Feed, user: User): Int
}