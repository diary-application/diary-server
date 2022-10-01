package diary.capstone.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@Controller
@ApiIgnore
class SwaggerController {
    @GetMapping("/api")
    fun redirectSwagger() = "redirect:/swagger-ui/#"
}

@Controller
class PageViewController {
    @GetMapping
    fun welcome() = "forward:/index.html"
}