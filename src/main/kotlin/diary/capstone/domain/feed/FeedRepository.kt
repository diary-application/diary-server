package diary.capstone.domain.feed

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FeedRepository: JpaRepository<Feed, Long> {
    fun findByShowScope(pageable: Pageable, showScope: String): Page<Feed>
}