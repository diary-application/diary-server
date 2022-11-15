package diary.capstone.domain.report

import diary.capstone.domain.mail.MailService
import diary.capstone.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReportService(
    private val reportRepository: ReportRepository,
    private val mailService: MailService
) {

    fun reportUser(targetUserId: Long, form: ReportDto, loginUser: User) {
        reportRepository.save(
            Report(
                reporterId = loginUser.id!!,
                targetUserId = targetUserId,
                type = form.type,
                reason = form.reason
            )
        )
        // TODO 메일 발송 로직(스케쥴러에 등록하거나 건당 메일 발송)
    }

    fun getAllReports(pageable: Pageable): Page<Report> =
        reportRepository.findAll(pageable)

    fun deleteReport(reportId: Long) =
        reportRepository.deleteById(reportId)
}