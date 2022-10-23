package diary.capstone

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import springfox.documentation.annotations.ApiIgnore

@Controller
@ApiIgnore
class PageViewController {
    @GetMapping(value = [
        "",
        "/",
        "/login",
        "/profile",
        "/profile/**",
        "/chat",
        "/schedule",
        "/setting"
    ])
    fun index() = "forward:/index.html"

    @GetMapping("/api")
    fun redirectSwagger() = "redirect:/swagger-ui/#"
}