package diary.capstone.util

import diary.capstone.auth.AuthManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 스케쥴러 클래스
 */
@Component
class ScheduledTasks(private val authManager: AuthManager) {

    // 5초마다 만료된 메일 인증 코드를 삭제한다
    @Scheduled(fixedRate = 5000)
    fun removeExpiredData() { authManager.removeExpiredCodesAndEmails() }
}