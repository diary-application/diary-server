package diary.capstone

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import springfox.documentation.annotations.ApiIgnore

@Controller
@ApiIgnore
class PageViewController {
    @GetMapping
    fun welcome() = "forward:/index.html"

    @GetMapping("/api")
    fun redirectSwagger() = "redirect:/swagger-ui/#"
}