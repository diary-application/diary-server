package diary.capstone.domain.user

import diary.capstone.auth.AuthService
import diary.capstone.auth.AuthManager
import diary.capstone.config.INTERESTS_LIMIT
import diary.capstone.domain.file.FileService
import diary.capstone.domain.occupation.INTERESTS_EXCEEDED
import diary.capstone.domain.occupation.OCCUPATION_NOT_FOUND
import diary.capstone.domain.occupation.OccupationException
import diary.capstone.domain.occupation.OccupationService
import diary.capstone.util.MailService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import kotlin.math.min

@Service
class LoginService(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val authManager: AuthManager,
    private val mailService: MailService
) {

    @Transactional
    fun login(form: LoginForm, request: HttpServletRequest): User {
        val user = userRepository.findByUidAndPassword(form.uid, form.password)
            ?: throw AuthException(LOGIN_FAILED)

        /**
         * 다른 ip로 접속 시 or 로그인 하려는 유저가 로그인 대기 상태일 시
         * 인증 코드 생성 -> 인증 메일 발송 -> 인증 예외 발생
         */
        if (user.ip != request.remoteAddr || user.loginWaiting) {
            user.update(loginWaiting = true)
            mailService.sendLoginAuthMail(authManager.generateCode(user.id!!.toString()), user.email)
            throw AuthException(MAIL_AUTH_REQUIRED)
        }
        else authService.login(request, user)

        return user
    }

    // 메일 인증 로그인
    @Transactional
    fun mailAuthenticationLogin(form: MailAuthLoginForm, request: HttpServletRequest): User {
        val user = userRepository.findByUidAndPassword(form.uid, form.password)
            ?: throw AuthException(LOGIN_FAILED)

        // 인증 코드 일치 확인, 불일치 시 예외 발생
        if (form.code == authManager.getAuthCode(user.id!!.toString())) {
            // 최근 접속 IP를 현재 접속 IP로 수정, 로그인 대기 상태 해제 후 로그인
            authManager.removeUsedAuthCode(user.id!!.toString())
            user.update(ip = request.remoteAddr, loginWaiting = false)
            return authService.login(request, user)
        }
        else throw AuthException(AUTH_CODE_MISMATCH)
    }
    
    // 이메일 인증 메일 발송
    fun sendEmailAuthMail(form: AuthMailForm) =
        mailService.sendEmailAuthMail(authManager.generateCode(form.email), form.email)

    // 이메일 인증 메일 확인
    fun checkEmailAuthMail(form: AuthCodeForm) {
        if (form.code == authManager.getAuthCode(form.email)) {
            authManager.addAuthenticatedEmail(form.email)
            authManager.removeUsedAuthCode(form.email)
        }
        else throw UserException(AUTH_CODE_MISMATCH)
    }

    // 유저 생성
    @Transactional
    fun join(form: JoinForm, request: HttpServletRequest): User {
        if (!form.checkPassword()) throw UserException(PASSWORD_MISMATCH)
        if (userRepository.existsByUid(form.uid)) throw UserException(DUPLICATE_ID)
        if (userRepository.existsByEmail(form.email)) throw UserException(DUPLICATE_EMAIL)
        if (!authManager.emails.contains(form.email)) throw UserException(MAIL_AUTH_REQUIRED)

        // 로그인 대기 상태로 유저 생성
        userRepository.save(
            User(
                uid = form.uid,
                password = form.password,
                name = form.name,
                email = form.email,
                ip = request.remoteAddr
            )
        )

        return login(LoginForm(form.uid, form.password), request)
    }

    fun logout(request: HttpServletRequest): Boolean =
        authService.logout(request)
}

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val occupationService: OccupationService,
    private val fileService: FileService
) {

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User =
        userRepository.findById(userId).orElseThrow { throw UserException(USER_NOT_FOUND) }

    // 내가 팔로우 중인 유저 목록
    @Transactional(readOnly = true)
    fun getFollowing(pageable: Pageable, userId: Long): Page<User> =
        getPagedUsers(pageable,
            getUser(userId).following
                .map { it.target }
                .sortedBy { it.name }
        )

    // 나를 팔로우 중인 유저 목록
    @Transactional(readOnly = true)
    fun getFollowers(pageable: Pageable, userId: Long): Page<User> =
        getPagedUsers(pageable,
            getUser(userId).follower
                .map { it.user }
                .sortedBy { it.name }
        )

    private fun getPagedUsers(pageable: Pageable, users: List<User>): Page<User> {
        val total = users.size
        val start = pageable.offset.toInt()
        val end = min((start + pageable.pageSize), total)
        return PageImpl(users.subList(start, end), pageable, total.toLong())
    }

    fun followUser(userId: Long, loginUser: User): Boolean {
        // 자기 자신은 팔로우 불가능
        if (loginUser.id == userId) throw UserException(FOLLOW_TARGET_INVALID)
        
        val targetUser = getUser(userId)

        // 이미 팔로우 한 대상은 팔로우 불가능
        if (loginUser.following.none { it.target.id == targetUser.id })
            loginUser.following.add(Follow(user = loginUser, target = targetUser))
        else throw UserException(ALREADY_FOLLOWED)
        return true
    }

    fun unfollowUser(userId: Long, loginUser: User): Boolean {
        loginUser.following.remove(
            loginUser.following
                .find { it.user.id == loginUser.id && it.target.id == getUser(userId).id }
        )
        return true
    }

    fun updateUserInfo(form: UserInfoUpdateForm, loginUser: User): User {
        return loginUser.update(
            name = form.name,
            email = form.email,
        )
    }

    fun updateUserOccupation(form: UserOccupationUpdateForm, loginUser: User): User {
        return loginUser.update(
            occupation = occupationService.getOccupation(form.occupation)
        )
    }

    fun updateUserInterests(form: UserInterestsUpdateForm, loginUser: User): User {
        if (form.interests.size > INTERESTS_LIMIT) throw OccupationException(INTERESTS_EXCEEDED)
        // 해당 직종이 존재하는지 확인
        form.interests.forEach {
            if (!occupationService.isExists(it)) throw OccupationException("[$it] $OCCUPATION_NOT_FOUND")
        }
        return loginUser.update(
            interests = form.interests.joinToString(",")
        )
    }

    fun updateProfileImage(image: MultipartFile, loginUser: User): User {
        // 등록된 프로필 사진이 있다면 원래 사진 삭제 후 새 사진 저장
        loginUser.profileImage?.let { fileService.deleteFile(it) }
        return loginUser.update(profileImage = fileService.saveFile(image))
    }

    fun updatePassword(form: PasswordUpdateForm, loginUser: User): Boolean {
        if (!form.checkPassword()) throw UserException(NEW_PASSWORD_MISMATCH)
        if (form.currentPassword != loginUser.password) throw UserException(CURRENT_PASSWORD_MISMATCH)
        loginUser.update(form.currentPassword)
        return true
    }

    fun deleteUser(form: UserDeleteForm, loginUser: User): Boolean {
        userRepository.delete(loginUser)
        return true
    }
}