package diary.capstone.domain.user

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class UserServiceTest {

    @Autowired lateinit var userService: UserService
    @Autowired lateinit var userRepository: UserRepository

    @Test
    fun getUserById() {
        val userId = 1L
        userService.getUserById(userId)
    }

    @Test
    fun searchUser() {
        val userId = 1L
    }

    @Test
    fun getFollowing() {
        val userId = 1L
        val pageable = PageRequest.of(0, 15)
        val result = userService.getFollowing(pageable, userId)
        println("result = ${result.content}")
    }

    @Test
    fun getFollowers() {
        val userId = 1L
        val pageable = PageRequest.of(0, 10)

    }

    @Test
    fun followUser() {
    }

    @Test
    fun unfollowUser() {
    }

    @Test
    fun updateUserName() {
    }

    @Test
    fun updateUserMessage() {
    }

    @Test
    fun updateUserOccupation() {
    }

    @Test
    fun updateUserInterests() {
    }

    @Test
    fun updateProfileImage() {
    }

    @Test
    fun deleteProfileImage() {
    }

    @Test
    fun updateUserProfileShow() {
    }

    @Test
    fun updatePassword() {
    }

    @Test
    fun deleteUser() {
    }
}