package diary.capstone.util.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import springfox.documentation.annotations.ApiIgnore

@Controller
@ApiIgnore
class SwaggerRedirector {
    @GetMapping("/api")
    fun redirectSwagger(): String = "redirect:/swagger-ui/#"
}