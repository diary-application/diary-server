package diary.capstone.domain.occupation

import org.springframework.data.jpa.repository.JpaRepository

interface OccupationRepository: JpaRepository<Occupation, Long> {

    fun findByName(name: String): Occupation?
    fun existsByName(name: String): Boolean
    fun deleteByName(name: String): Int
}