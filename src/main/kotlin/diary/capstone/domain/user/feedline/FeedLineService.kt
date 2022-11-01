package diary.capstone.domain.user.feedline

import diary.capstone.domain.user.FEED_LINE_NOT_FOUND
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedLineService {

    fun createFeedLine(form: FeedLineRequestForm, loginUser: User) {

    }

    fun updateFeedLine(feedLineId: Long, form: FeedLineRequestForm, loginUser: User) {

    }

    fun deleteFeedLine(feedLineId: Long, loginUser: User): Boolean =
        loginUser.feedLines
            .find { it.id == feedLineId }
            ?.let { loginUser.feedLines.remove(it) }
            ?:run { throw UserException(FEED_LINE_NOT_FOUND) }
}