package diary.capstone.config

import diary.capstone.auth.AuthService
import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.NOT_LOGIN_USER
import diary.capstone.domain.user.User
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

    // 인증 서비스의 getUser 메소드를 통해 로그인 한 유저 엔티티를 얻어 파라미터 값에 바인딩
    override fun resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer?,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory?
    ): User {
        return authService.getUser(webRequest.nativeRequest as HttpServletRequest)
            ?: throw AuthException(NOT_LOGIN_USER)
    }
}