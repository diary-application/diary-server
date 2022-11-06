package diary.capstone.domain.notice

import diary.capstone.domain.user.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import springfox.documentation.annotations.ApiIgnore

@RestController
class NoticeController(private val noticeService: NoticeService) {

//    @GetMapping(value = ["/notice"], produces = ["text/event-stream"])
//    fun noticeSubscribe(@ApiIgnore user: User): SseEmitter {
//
//    }
}