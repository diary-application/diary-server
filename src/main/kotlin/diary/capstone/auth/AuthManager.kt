package diary.capstone.auth

import diary.capstone.config.AUTH_CODE_DIGITS
import diary.capstone.config.AUTH_VALID_MINUTE
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 인증 코드 및 인증된 메일을 관리하는 클래스
 * tables, emails 요소들은 AUTH_VALID_MINUTE 분 만큼 유지되며, 이후 삭제된다.
 */
@Component
class AuthManager {

    // 인증 코드 생성에 사용될 문자들
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    
    // 식별문자열, Pair(코드, 발급일)로 구성된 코드 관리 테이블
    val table: MutableMap<String, Pair<String, LocalDateTime>> = mutableMapOf()

    // 인증된 이메일들을 보관하는 맵
    val emails: MutableMap<String, LocalDateTime> = mutableMapOf()

    // 인증용 n자리 코드 생성, 반환
    fun generateCode(key: String): String {
        var code = ""
        repeat(AUTH_CODE_DIGITS) { code += chars.random() }
        this.table[key] = Pair(code, LocalDateTime.now())
        return code
    }

    // 해당 유저의 인증 코드 반환
    fun getAuthCode(key: String): String? = this.table[key]?.first

    // 인증된 메일에 해당 메일 추가
    fun addAuthenticatedEmail(email: String) { this.emails[email] = LocalDateTime.now() }

    // 메일 코드 인증 시 해당 코드 삭제
    fun removeUsedAuthCode(key: String) = this.table.remove(key)
    
    // 유효 기간이 지난 코드, 이메일 삭제
    fun removeExpiredCodes() {
        table.forEach {
            if (ChronoUnit.MINUTES.between(it.value.second, LocalDateTime.now()) > AUTH_VALID_MINUTE)
                table.remove(it.key)
        }
        emails.forEach {
            if (ChronoUnit.MINUTES.between(it.value, LocalDateTime.now()) > AUTH_VALID_MINUTE)
                emails.remove(it.key)
        }
    }
}