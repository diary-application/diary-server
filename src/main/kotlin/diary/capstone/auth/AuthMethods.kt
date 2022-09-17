package diary.capstone.auth

import diary.capstone.domain.user.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

/**
 * 클라이언트 인증 방식 구현체 모음
 */

const val AUTH_KEY = "user"

// 세션 방식
@Service
@Transactional(readOnly = true)
class SessionMethod(private val userRepository: UserRepository): AuthService {

    override fun login(request: HttpServletRequest, uid: String, password: String): User {
        userRepository.findByUidAndPassword(uid, password)?.let {
            request.getSession(true).setAttribute(AUTH_KEY, it.uid)
            return it
        } ?: run { throw UserException(LOGIN_FAILED) }
    }

    override fun logout(request: HttpServletRequest): Boolean {
        request.session.invalidate()
        return true
    }

    override fun getUser(request: HttpServletRequest): User? =
        userRepository.findByUid(
            request.session.getAttribute(AUTH_KEY)?.let { it.toString() } ?: ""
        )

    override fun authCheck(request: HttpServletRequest) {
        request.session.getAttribute(AUTH_KEY) ?: throw AuthException(NOT_LOGIN_USER)
    }
}