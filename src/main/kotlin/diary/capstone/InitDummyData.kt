package diary.capstone

import diary.capstone.domain.feed.*
import diary.capstone.domain.occupation.OccupationService
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Component
@Transactional
class InitDummyData(
    private val userRepository: UserRepository,
    private val occupationService: OccupationService,
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
        
        // 관리자 생성
        userRepository.saveAndFlush(
            User(
                uid = "admin",
                password = "1234",
                email = "admin@mail.com",
                name = "관리자"
            )
        )
        
        occupationService.createOccupation("IT")
        occupationService.createOccupation("엔터테인먼트")

        // 댓글 생성
//        repeat(11) {
//            feedService.createRootComment(feed.id!!, CommentRequestForm("댓글${it + 1}.."), user2)
//        }
//        repeat(11) {
//            feedService.createChildComment(feed.id!!, 1L, CommentRequestForm("대댓글${it + 1}.."), user2)
//        }
    }
}