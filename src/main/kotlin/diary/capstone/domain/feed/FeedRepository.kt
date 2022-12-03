package diary.capstone.domain.feed

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FeedRepository: JpaRepository<Feed, Long> {
    fun findByShowScope(pageable: Pageable, showScope: String): Page<Feed>

    @Query("SELECT f FROM Feed f WHERE f.writer.id = :userId")
    fun searchAll(@Param("userId") userId: Long, pageable: Pageable): Page<Feed>
}