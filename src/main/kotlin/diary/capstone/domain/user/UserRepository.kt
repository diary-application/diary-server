package diary.capstone.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository: JpaRepository<User, Long> {
    fun findByUidAndPassword(uid: String, password: String): User?
    fun findByUid(uid: String): User?
    fun existsByUid(uid: String): Boolean
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    fun searchAllByName(pageable: Pageable, @Param("keyword") keyword: String): Page<User>
}