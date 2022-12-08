package diary.capstone.domain.feed

import org.springframework.data.mongodb.repository.MongoRepository

interface FeedRepositoryM: MongoRepository<FeedM, Long> {

    fun findAllByWriter(writer: Long): List<FeedM>
}