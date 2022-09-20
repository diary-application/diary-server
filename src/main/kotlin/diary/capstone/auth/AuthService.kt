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
     * 요청자의 로그인 상태 체크
     * 로그인 안되어있다면 예외 발생
     */
    fun authCheck(request: HttpServletRequest)

    /**
     * 요청자가 관리자인지 체크
     * 관리자가 아니라면 예외 발생
     */
    fun adminCheck(request: HttpServletRequest)
}