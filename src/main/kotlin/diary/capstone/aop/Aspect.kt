package diary.capstone.aop

import diary.capstone.auth.AuthService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class Aspect(private val authService: AuthService) {

    /**
     * Auth 어노테이션이 부착된 클래스, 메소드의 실행 전
     * 인증 실패 시 예외 발생
     */
    @Before("@within(diary.capstone.auth.Auth) || @annotation(diary.capstone.auth.Auth)")
    fun authCheck() {
        val request = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        authService.authCheck(request.request)
    }
}