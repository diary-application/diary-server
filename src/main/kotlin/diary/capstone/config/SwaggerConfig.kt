package diary.capstone.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

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