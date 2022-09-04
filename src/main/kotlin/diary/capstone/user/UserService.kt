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

    @Transactional(readOnly = true)
    fun getFollowing(pageable: Pageable, loginUser: User): Page<User> =
        getPagedUsers(pageable,
            loginUser.following
                .filter { it.user.id == loginUser.id }
                .map { getUser(it.user.id!!) }
                .sortedBy { it.name }
        )

    @Transactional(readOnly = true)
    fun getFollowers(pageable: Pageable, loginUser: User): Page<User> =
        getPagedUsers(pageable,
            loginUser.follower
                .filter { it.followUser.id == loginUser.id }
                .map { getUser(it.user.id!!) }
                .sortedBy { it.name }
        )

    private fun getPagedUsers(pageable: Pageable, users: List<User>): Page<User> {
        val total = users.size
        val start = pageable.offset.toInt()
        val end = min((start + pageable.pageSize), total)
        return PageImpl(users.subList(start, end), pageable, total.toLong())
    }

    fun followUser(userId: Long, loginUser: User): Boolean {
        val targetUser = getUser(userId)
        if (loginUser.following.none { it.user.id == loginUser.id && it.followUser.id == targetUser.id })
            loginUser.following.add(Follow(user = loginUser, followUser = targetUser))
        else throw UserException(ALREADY_FOLLOWED)
        return true
    }

    fun unfollowUser(userId: Long, loginUser: User): Boolean {
        loginUser.following.remove(
            loginUser.following
                .find { it.user.id == loginUser.id && it.followUser.id == getUser(userId).id }
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