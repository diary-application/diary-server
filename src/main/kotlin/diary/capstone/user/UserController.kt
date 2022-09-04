package diary.capstone.user

import diary.capstone.util.BoolResponse
import diary.capstone.util.aop.Auth
import diary.capstone.util.exception.ValidationException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginForm: LoginForm,
              bindingResult: BindingResult,
              request: HttpServletRequest
    ): UserDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return UserDetailResponse(authService.login(loginForm, request))
    }

    @PostMapping("/join")
    fun join(@Valid @RequestBody joinForm: JoinForm,
             bindingResult: BindingResult,
             request: HttpServletRequest
    ): UserDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return UserDetailResponse(authService.join(joinForm, request))
    }

    @Auth
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): BoolResponse {
        request.session.invalidate()
        return BoolResponse(true)
    }
}

@Auth
@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUser(user: User) = UserDetailResponse(user)

    @GetMapping("/{userId}")
    fun getUser(@PathVariable("userId") userId: Long) =
        UserDetailResponse(userService.getUser(userId))

    // 내가 팔로우한 사람 목록 조회
    @GetMapping("/following")
    fun getFollowing(@PageableDefault pageable: Pageable, user: User) =
        UserPagedResponse(userService.getFollowing(pageable, user))

    // 나를 팔로우한 사람 목록 조회
    @GetMapping("/follower")
    fun getFollowers(@PageableDefault pageable: Pageable, user: User) =
        UserPagedResponse(userService.getFollowers(pageable, user))

    // 해당 유저 팔로우
    @PatchMapping("/follow/{userId}")
    fun followUser(@PathVariable("userId") userId: Long, user: User) =
        BoolResponse(userService.followUser(userId, user))

    // 해당 유저 팔로우 취소
    @DeleteMapping("/follow/{userId}")
    fun unfollowUser(@PathVariable("userId") userId: Long, user: User) =
        BoolResponse(userService.unfollowUser(userId, user))

    @PatchMapping("/password")
    fun updatePassword(form: PasswordUpdateForm, user: User) =
        BoolResponse(userService.updatePassword(form, user))

    @PatchMapping("/info")
    fun updateUserInfo(form: UserInfoUpdateForm, user: User) =
        UserDetailResponse(userService.updateUserInfo(form, user))

    // 회원 탈퇴 (비밀번호와 함께 요청)
    @DeleteMapping
    fun deleteUser(form: UserDeleteForm, user: User, request: HttpServletRequest): BoolResponse {
        userService.deleteUser(form, user)
        request.session.invalidate()
        return BoolResponse(true)
    }
}