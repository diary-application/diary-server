package diary.capstone.aop

import diary.capstone.auth.JwtProvider
import diary.capstone.config.ADMIN_EMAIL
import diary.capstone.domain.user.ADMIN_ONLY
import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.INVALID_TOKEN
import diary.capstone.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class Aspect(private val jwtProvider: JwtProvider) {

    /**
     * domain 내 모든 메소드 실행 시간 측정
     */
/*    @Around("execution(* diary.capstone.domain..*.*(..))")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        try { return joinPoint.proceed() }
        finally {
            logger().info(
                "[${joinPoint.signature.name} At ${joinPoint.signature.declaringType.simpleName}] : " +
                        "${System.currentTimeMillis() - start}ms taken"
            )
        }
    }*/

    /**
     * Auth 어노테이션이 부착된 클래스, 메소드의 실행 전
     * 요청자가 로그인 되어있지 않다면 예외 발생
     */
    @Before("@within(diary.capstone.auth.Auth) || @annotation(diary.capstone.auth.Auth)")
    fun authCheck() {
        val request = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        jwtProvider.extractToken(request.request)?.let {
            if (!jwtProvider.validateToken(it)) throw AuthException(INVALID_TOKEN)
        } ?: run { throw AuthException(INVALID_TOKEN) }
    }

    /**
     * Admin 어노테이션이 부착된 클래스, 메소드의 실행 전
     * 요청자가 관리자가 아니라면 예외 발생
     */
    @Before("@within(diary.capstone.auth.Admin) || @annotation(diary.capstone.auth.Admin)")
    fun adminCheck() {
        val request = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        jwtProvider.extractToken(request.request)?.let {
            jwtProvider.validateToken(it)
            if (jwtProvider.getSubject(it) != ADMIN_EMAIL) throw AuthException(ADMIN_ONLY)
        } ?: run { throw AuthException(INVALID_TOKEN) }
    }
}