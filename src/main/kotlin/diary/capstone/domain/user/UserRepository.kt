package diary.capstone.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    fun searchAllByName(pageable: Pageable, @Param("keyword") keyword: String): Page<User>
}