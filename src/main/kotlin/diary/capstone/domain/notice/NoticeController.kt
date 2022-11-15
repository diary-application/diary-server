package diary.capstone.domain.notice

import diary.capstone.domain.user.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/notice")
class NoticeController(private val noticeService: NoticeService) {

    @GetMapping
    fun getNotifications(
        @PageableDefault pageable: Pageable,
        @ApiIgnore user: User
    ) = NoticePagedResponse(noticeService.getAllNotifications(pageable, user))

    @DeleteMapping("/{noticeId}")
    fun deleteNotice(
        @PathVariable("noticeId") noticeId: Long,
        @ApiIgnore user: User
    ) = noticeService.deleteNotice(noticeId, user)
}