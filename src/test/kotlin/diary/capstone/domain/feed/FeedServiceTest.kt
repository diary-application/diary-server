package diary.capstone.domain.feed

import com.querydsl.jpa.impl.JPAQueryFactory
import diary.capstone.domain.user.UserService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import diary.capstone.domain.feed.QFeed.feed
import diary.capstone.domain.file.QFile.file
import diary.capstone.domain.feed.QFeedLike.feedLike
import diary.capstone.domain.user.QUser.user
import diary.capstone.domain.user.User

@SpringBootTest
@Transactional
internal class FeedServiceTest {

    @Autowired lateinit var feedService: FeedService
    @Autowired lateinit var feedRepository: FeedRepository
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var userRepository: FeedRepository
    @Autowired lateinit var jpaQueryFactory: JPAQueryFactory

    @Test @DisplayName("내 피드 모두 조회")
    fun getFeedsMy() {
        val userId = 1L
        val user = userService.getUserById(userId)
        val pageable = PageRequest.of(0, 5)
        val result = feedService.getFeeds(pageable, userId, null, user)
        result.content.forEach {
            println(it.id.toString() + ": " + it.content)
        }
    }
    @Test @DisplayName("다른 유저 피드 모두 조회")
    fun getFeedsOther() {
        val userId = 1L
        val user = userService.getUserById(userId)
        val pageable = PageRequest.of(0, 5)
        val result = feedService.getFeeds(pageable, 2L, null, user)
        result.content.forEach {
            println(it.id.toString() + ": " + it.content)
        }
    }
    @Test @DisplayName("전체 공개 피드 모두 조회")
    fun getFeedsAll() {
        val userId = 1L
        val user = userService.getUserById(userId)
        val pageable = PageRequest.of(0, 5)
        val result = feedService.getFeeds(pageable, null, null, user)
        result.content.forEach {
            println(it.id.toString() + ": " + it.content)
        }
    }

    @Test @DisplayName("피드 검색")
    fun searchFeedsByUserAndKeyword() {
        val userId = 1L
        val keyword = "저"
        val pageable = PageRequest.of(0, 2)

        // querydsl fetch join 으로 해결, 페이징은 Pageable 의 offset, pageSize 로 해결
        val qResult = jpaQueryFactory
            .selectFrom(feed).distinct()
            .leftJoin(feed.files, file).fetchJoin()
            .where(
                feed.writer.id.eq(userId).and(
                    file.description.like("%$keyword%").or(feed.content.like("%$keyword%"))
                )
            )
            .orderBy(feed.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        qResult.forEach {
            println(it.id)
        }
    }

    @Test @DisplayName("피드 하나 조회")
    fun getFeed() {
        val feedId = 1L
        val feed = feedService.getFeed(feedId)
    }

    @Test @DisplayName("피드 좋아요 목록 조회")
    fun getFeedLikes() {
        val feedId = 1L
        val pageable = PageRequest.of(0, 2)

        val qResult = jpaQueryFactory
            .select(feedLike.user.id)
            .from(feedLike)
            .where(feedLike.feed.id.eq(feedId))
            .fetch()

        val likedUsers = jpaQueryFactory
            .selectFrom(user)
            .where(user.id.`in`(qResult))
            .fetch()

        likedUsers.forEach {
            println(it.name)
        }
    }
    
    @Test @DisplayName("루트 댓글 목록 조회")
    fun getRootComments() {
    }

    @Test @DisplayName("내 댓글 목록 조회")
    fun getMyComments() {
    }

    @Test @DisplayName("대댓글 목록 조회")
    fun getChildComments() {
    }

    @Test @DisplayName("댓글 좋아요 목록 조회")
    fun getCommentLikes() {
    }
}