package diary.capstone.domain.report

import diary.capstone.auth.Admin
import diary.capstone.auth.Auth
import diary.capstone.domain.user.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/report")
class ReportController(private val reportService: ReportService) {

    @Auth
    @PostMapping("/{userId}")
    fun reportUser(
        @PathVariable("userId") userId: Long,
        @ApiIgnore user: User,
        @RequestBody form: ReportDto
    ) = reportService.reportUser(userId, form, user)

    @Admin
    @GetMapping
    fun getAllReports(@PageableDefault(size = 15) pageable: Pageable) =
        reportService.getAllReports(pageable)

    @Admin
    @DeleteMapping("/{reportId}")
    fun deleteReport(@PathVariable("reportId") reportId: Long) =
        reportService.deleteReport(reportId)
}