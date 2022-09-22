package diary.capstone.util

import diary.capstone.auth.AuthManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 스케쥴러 클래스
 */
@Component
class Scheduler(private val authManager: AuthManager) {

    // 5초마다 만료된 메일 인증 코드를 삭제한다
    @Scheduled(fixedRate = 5000)
    fun remove() {
        logger().info("table: ${authManager.table.map { it.key }}")
        logger().info("emails: ${authManager.emails.map { it.key }}")
        authManager.removeExpiredCodes()
    }
}