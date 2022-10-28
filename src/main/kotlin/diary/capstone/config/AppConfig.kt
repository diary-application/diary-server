package diary.capstone.config

import com.querydsl.jpa.impl.JPAQueryFactory
import diary.capstone.auth.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class AppConfig(private val jwtProvider: JwtProvider): WebMvcConfigurer {
    
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

// JPAQueryFactory 빈 등록
@Configuration
class QuerydslConfig(@PersistenceContext private val em: EntityManager) {
    @Bean fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(this.em)
}

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
            .description("모두의 일기장 API 명세서")
            .build()
    }
}