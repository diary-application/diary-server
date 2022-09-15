package diary.capstone.user

import diary.capstone.util.AUTH_KEY
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest
import kotlin.math.min

@Service
@Transactional
class AuthService(private val userRepository: UserRepository) {

    fun login(loginForm: LoginForm, request: HttpServletRequest): User {
        userRepository.findByUidAndPassword(loginForm.uid, loginForm.password)?.let {
            val session = request.getSession(true)
            session.setAttribute(AUTH_KEY, it.uid)
            return it
        } ?: run { throw UserException(LOGIN_FAILED) }
    }

    // 유저 생성 후 로그인
    fun join(joinForm: JoinForm, request: HttpServletRequest): User {
        if (!joinForm.checkPassword()) throw UserException(PASSWORD_MISMATCH)
        if (userRepository.existsByUid(joinForm.uid)) throw UserException(DUPLICATE_ID)

        val user = userRepository.save(
            User(
                uid = joinForm.uid,
                password = joinForm.password,
                name = joinForm.name,
                email = joinForm.email
            )
        )

        return login(LoginForm(user.uid, user.password), request)
    }
}

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {

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

    fun updatePassword(form: PasswordUpdateForm, loginUser: User): Boolean {
        if (!form.checkPassword()) throw UserException(PASSWORD_MISMATCH)
        if (form.currentPassword != loginUser.password) throw UserException(PASSWORD_MISMATCH)
        loginUser.update(form.currentPassword)
        return true
    }

    fun updateUserInfo(form: UserInfoUpdateForm, loginUser: User): User {
        return loginUser.update(
            name = form.name,
            email = form.email,
            job = form.job,
            category = form.category
        )
    }

    fun deleteUser(form: UserDeleteForm, loginUser: User): Boolean {
        if (form.password == loginUser.password) userRepository.delete(loginUser)
        return true
    }
}