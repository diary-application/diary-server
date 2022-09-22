package diary.capstone.domain.user

import diary.capstone.util.BoolResponse
import diary.capstone.auth.Auth
import diary.capstone.auth.AuthService
import diary.capstone.util.SuccessResponse
import diary.capstone.util.ok
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class LoginController(private val loginService: LoginService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody form: LoginForm, request: HttpServletRequest) =
        UserDetailResponse(loginService.login(form, request))

    // 메일 인증과 함께 로그인
    @PostMapping("/mail-auth-login")
    fun mailAuthenticationLogin(@Valid @RequestBody form: MailAuthLoginForm, request: HttpServletRequest) =
        UserDetailResponse(loginService.mailAuthenticationLogin(form, request))

    // 회원 가입, 이메일은 인증된 이메일을 입력해야 함
    @PostMapping("/join")
    fun join(@Valid @RequestBody form: JoinForm, request: HttpServletRequest) =
        UserDetailResponse(loginService.join(form, request))
    
    // 이메일 인증 메일 발송
    @PostMapping("/mail-auth")
    fun authenticationEmail(@Valid @RequestBody form: AuthMailForm): SuccessResponse {
        loginService.sendEmailAuthMail(form)
        return SuccessResponse("확인용 메일이 발송되었습니다.")
    }

    // 이메일 인증 메일 확인
    @PostMapping("/mail-auth-check")
    fun checkEmailAuthMail(@Valid @RequestBody form: AuthCodeForm): SuccessResponse {
        loginService.checkEmailAuthMail(form)
        return SuccessResponse("해당 메일이 인증되었습니다.")
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): BoolResponse =
        BoolResponse(loginService.logout(request))
}

@Auth
@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val authService: AuthService
) {
    // 내 정보 조회
    @GetMapping
    fun getUser(user: User) = UserDetailResponse(user)

    // 특정 유저의 유저 정보 조회
    @GetMapping("/{userId}")
    fun getUser(@PathVariable("userId") userId: Long) =
        UserDetailResponse(userService.getUser(userId))

    // 특정 유저가 팔로우 한 사람 목록 조회
    @GetMapping("/{userId}/following")
    fun getFollowing(@PageableDefault pageable: Pageable,
                     @PathVariable("userId") userId: Long) =
        UserPagedResponse(userService.getFollowing(pageable, userId))

    // 특정 유저를 팔로우 한 사람 목록 조회
    @GetMapping("/{userId}/follower")
    fun getFollowers(@PageableDefault pageable: Pageable,
                     @PathVariable("userId") userId: Long) =
        UserPagedResponse(userService.getFollowers(pageable, userId))

    // 해당 유저 팔로우
    @PostMapping("/{userId}/follow")
    fun followUser(@PathVariable("userId") userId: Long, user: User) =
        BoolResponse(userService.followUser(userId, user))

    // 해당 유저 팔로우 취소
    @DeleteMapping("/{userId}/follow")
    fun unfollowUser(@PathVariable("userId") userId: Long, user: User) =
        BoolResponse(userService.unfollowUser(userId, user))

    // 내 정보 수정(이름, 메일)
    @PutMapping("/info")
    fun updateUserInfo(form: UserInfoUpdateForm, user: User) =
        UserDetailResponse(userService.updateUserInfo(form, user))

    // 직종 수정
    @PutMapping("/occupation")
    fun updateUserOccupation(@Valid @RequestBody form: UserOccupationUpdateForm, user: User) =
        UserDetailResponse(userService.updateUserOccupation(form, user))
    
    // 관심 분야 수정
    @PutMapping("/interests")
    fun updateUserInterests(@RequestBody form: UserInterestsUpdateForm, user: User) =
        UserDetailResponse(userService.updateUserInterests(form, user))

    // 프로필 이미지 변경
    @PutMapping("/profile-image")
    fun updateProfileImage(@RequestPart("image") image: MultipartFile, user: User) =
        UserDetailResponse(userService.updateProfileImage(image, user))

    // 비밀번호 변경
    @PutMapping("/password")
    fun updatePassword(form: PasswordUpdateForm, user: User) =
        BoolResponse(userService.updatePassword(form, user))

    // 회원 삭제 (비밀번호와 함께 요청)
    @DeleteMapping
    fun deleteUser(@RequestBody form: UserDeleteForm, user: User, request: HttpServletRequest): BoolResponse {
        if (form.password == user.password) {
            userService.deleteUser(form, user)
            authService.logout(request)
            return BoolResponse(true)
        }
        else throw UserException(PASSWORD_MISMATCH)
    }
}