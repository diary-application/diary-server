package diary.capstone.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    fun findByUidAndPassword(uid: String, password: String): User?
    fun findByUid(uid: String): User?
    fun existsByUid(uid: String): Boolean
}