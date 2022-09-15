package diary.capstone

import diary.capstone.domain.feed.*
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Component
@Transactional
class InitDummyData(
    private val userRepository: UserRepository,
    private val feedService: FeedService
) {

    @PostConstruct
    fun databaseInitializer() {
        // 유저 생성
        repeat(3) {
            userRepository.saveAndFlush(
                User(
                    uid = "user${it + 1}",
                    password = "1234",
                    email = "user${it + 1}@mail.com",
                    name = "사용자${it + 1}"
                )
            )
        }
        val user1 = userRepository.findById(1).get()
        val user2 = userRepository.findById(2).get()
        
        // 피드 생성
        val feed = feedService.createFeed(
            FeedRequestForm("모두 공개", SHOW_ALL), user1
        )
        feedService.createFeed(
            FeedRequestForm("팔로워 공개", SHOW_FOLLOWERS), user1
        )
        feedService.createFeed(
            FeedRequestForm("나만 공개", SHOW_ME), user1
        )

        // 댓글 생성
//        repeat(11) {
//            feedService.createRootComment(feed.id!!, CommentRequestForm("댓글${it + 1}.."), user2)
//        }
//        repeat(11) {
//            feedService.createChildComment(feed.id!!, 1L, CommentRequestForm("대댓글${it + 1}.."), user2)
//        }
    }
}