package diary.capstone.auth

import diary.capstone.domain.user.*
import diary.capstone.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

/**
 * 클라이언트 인증 방식 구현체 모음
 */

const val AUTH_KEY = "user"

// 세션 방식
@Service
class SessionMethod(private val userRepository: UserRepository): AuthService {

    override fun login(request: HttpServletRequest, user: User): User {
        request.getSession(true).setAttribute(AUTH_KEY, user.uid)
        return user
    }

    @Transactional(readOnly = true)
    override fun logout(request: HttpServletRequest): Boolean {
        request.session.invalidate()
        return true
    }

    @Transactional(readOnly = true)
    override fun getUser(request: HttpServletRequest): User? =
        userRepository.findByUid(
            request.session.getAttribute(AUTH_KEY)?.let { it.toString() } ?: ""
        )

    @Transactional(readOnly = true)
    override fun authCheck(request: HttpServletRequest) {
        request.session.getAttribute(AUTH_KEY) ?: throw AuthException(NOT_LOGIN_USER)
    }

    @Transactional(readOnly = true)
    override fun adminCheck(request: HttpServletRequest) {
        val uid = request.session.getAttribute(AUTH_KEY)?.let { it.toString() } ?: ""
        if (uid != "admin") throw AuthException(ADMIN_ONLY)
    }
}