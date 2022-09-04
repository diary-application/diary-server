package diary.capstone.util.config

import diary.capstone.user.AuthException
import diary.capstone.user.NOT_LOGIN_USER
import diary.capstone.user.User
import diary.capstone.user.UserRepository
import diary.capstone.util.AUTH_KEY
import diary.capstone.util.exception.ValidationException
import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest

// 요청하는 클라이언트가 로그인 한 유저 정보를 얻기 위한 ArgumentResolver
class LoginUserArgumentResolver(private val userRepository: UserRepository): HandlerMethodArgumentResolver {

    // 핸들러 메소드 파라미터에 User 엔티티 클래스가 존재할 경우
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == User::class.java

    // request 객체로부터 세션에 등록된 uid를 데이터베이스에서 조회하여 User 엔티티에 바인딩
    override fun resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer?,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory?
    ): User {
        val request: HttpServletRequest = webRequest.nativeRequest as HttpServletRequest
        val uid = request.session.getAttribute(AUTH_KEY)?.let { it.toString() } ?: ""
        return userRepository.findByUid(uid) ?: throw AuthException(NOT_LOGIN_USER)
    }
}

class BindingResultArgumentResolver: HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == BindingResult::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ) {
        var bindingResult: BindingResult

    }
}