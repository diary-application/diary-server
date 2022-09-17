package diary.capstone.auth

import diary.capstone.domain.user.User
import javax.servlet.http.HttpServletRequest

interface AuthService {

    fun login(request: HttpServletRequest, uid: String, password: String): User

    fun logout(request: HttpServletRequest): Boolean

    /**
     * 요청자의 유저 정보 얻는 메소드
     */
    fun getUser(request: HttpServletRequest): User?

    /**
     * 인증 체크 메소드
     * 요청자의 로그인 상태 체크, 로그인 안되어있다면 예외 발생하게 구현
     */
    fun authCheck(request: HttpServletRequest)
}