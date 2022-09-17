package diary.capstone.config

import diary.capstone.auth.AuthService
import diary.capstone.auth.SessionMethod
import diary.capstone.domain.file.FILE_SAVE_PATH
import diary.capstone.domain.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
class AppConfig(private val authService: AuthService): WebMvcConfigurer {
    
    // ArgumentResolver 등록
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(LoginUserArgumentResolver(authService))
    }

    // 정적 리소스 조회 경로 설정
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/file/**")
            .addResourceLocations("file:$FILE_SAVE_PATH")
    }
}

// JPAQueryFactory 빈 등록
//@Configuration
//class QuerydslConfig(@PersistenceContext private val em: EntityManager) {
//    @Bean
//    fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(this.em)
//}

// Swagger 2.0 등록
@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun swagger(): Docket =
        Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .forCodeGeneration(true)
            .select()
            .apis(RequestHandlerSelectors.basePackage("diary.capstone"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
            .enable(true)

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("모두의 일기장 API")
            .description("동양미래대학교 7팀 졸업작품 - 모두의 일기장 API 명세서")
            .build()
    }
}