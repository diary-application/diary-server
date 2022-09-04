package diary.capstone

import diary.capstone.feed.CommentRequestForm
import diary.capstone.feed.*
import diary.capstone.user.AuthService
import diary.capstone.user.JoinForm
import diary.capstone.user.User
import diary.capstone.user.UserRepository
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
        repeat(2) {
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
            FeedRequestForm("user1의 피드"), user1
        )

        // 댓글 생성
        repeat(11) {
            feedService.createRootComment(feed.id!!, CommentRequestForm("댓글${it + 1}.."), user2)
        }
        repeat(11) {
            feedService.createChildComment(feed.id!!, 1L, CommentRequestForm("대댓글${it + 1}.."), user2)
        }
    }
}