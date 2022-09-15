package diary.capstone.auth

import diary.capstone.domain.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthMethodSelector(private val userRepository: UserRepository) {

    // 인증 방식 구현체 설정
    @Bean
    fun authService(): AuthService {
        return SessionMethod(userRepository)
    }
}