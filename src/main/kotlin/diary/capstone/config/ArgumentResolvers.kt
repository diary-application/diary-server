package diary.capstone.config

import diary.capstone.auth.AuthService
import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.NOT_LOGIN_USER
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserRepository
import diary.capstone.util.AUTH_KEY
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest

// 요청하는 클라이언트가 로그인 한 유저 정보를 얻기 위한 ArgumentResolver
class LoginUserArgumentResolver(private val authService: AuthService): HandlerMethodArgumentResolver {

    // 핸들러 메소드 파라미터에 User 엔티티 클래스가 존재할 경우
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == User::class.java

    // request 객체로부터 세션에 등록된 uid를 데이터베이스에서 조회하여 User 엔티티에 바인딩
    override fun resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer?,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory?
    ): User {
        // 핸들러 메소드 user(요청자) 파라미터를 바인딩하기 위한 인증 로직
        val request: HttpServletRequest = webRequest.nativeRequest as HttpServletRequest
        return authService.getUser(request) ?: throw AuthException(NOT_LOGIN_USER)
    }
}