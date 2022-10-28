package diary.capstone.config

import diary.capstone.auth.JwtProvider
import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.NOT_LOGIN_USER
import diary.capstone.domain.user.USER_NOT_FOUND
import diary.capstone.domain.user.User
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest

@Configuration
class WebMvcConfig(private val jwtProvider: JwtProvider): WebMvcConfigurer {
    
    // ArgumentResolver 등록
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(LoginUserArgumentResolver(jwtProvider))
    }

    // 정적 리소스 조회 경로 설정: 현재 미사용, 스토리지를 S3 로 이전함
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry
//            .addResourceHandler("/resource/**")
//            .addResourceLocations("file:$FILE_SAVE_PATH")
//            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))
//    }
}

// 요청하는 클라이언트가 로그인 한 유저 정보를 얻기 위한 ArgumentResolver
class LoginUserArgumentResolver(private val jwtProvider: JwtProvider): HandlerMethodArgumentResolver {

    // 핸들러 메소드 파라미터에 User 엔티티 클래스가 존재할 경우
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == User::class.java

    // 인증 서비스의 getUser 메소드를 통해 로그인 한 유저 엔티티를 얻어 파라미터 값에 바인딩
    override fun resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer?,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory?
    ): User {
        return jwtProvider.extractToken(webRequest.nativeRequest as HttpServletRequest)?.let {
            jwtProvider.validateToken(it)
            jwtProvider.getUser(it) ?: throw AuthException(USER_NOT_FOUND)
        } ?: throw AuthException(NOT_LOGIN_USER)
    }
}