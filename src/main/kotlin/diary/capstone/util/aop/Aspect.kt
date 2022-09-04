package diary.capstone.util.aop

import diary.capstone.user.AuthException
import diary.capstone.user.NOT_LOGIN_USER
import diary.capstone.util.AUTH_KEY
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class Aspect {

    // Auth 어노테이션이 달린 클래스, 메소드의 실행 전
    @Before("@within(Auth) || @annotation(Auth)")
    fun authCheck() {
        val request = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        request.request.session.getAttribute(AUTH_KEY) ?: throw AuthException(NOT_LOGIN_USER)
    }
}