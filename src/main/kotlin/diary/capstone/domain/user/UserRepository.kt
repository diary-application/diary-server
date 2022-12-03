package diary.capstone.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository: JpaRepository<User, Long> {

    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    /**
     * 검색 정책
     * 이름: 입력받은 문자열을 포함하는 모든 유저
     * 이메일: 입력받은 문자열을 맨 앞에 포함하는 모든 유저
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% OR u.email LIKE :email%")
    fun searchAllByNameOrEmail(
        pageable: Pageable,
        @Param("name") name: String,
        @Param("email") email: String
    ): Page<User>
}