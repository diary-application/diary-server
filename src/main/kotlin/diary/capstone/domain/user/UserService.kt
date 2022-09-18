package diary.capstone.domain.user

import diary.capstone.auth.AuthService
import diary.capstone.domain.file.FileService
import diary.capstone.domain.occupation.OCCUPATION_NOT_FOUND
import diary.capstone.domain.occupation.OccupationException
import diary.capstone.domain.occupation.OccupationService
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
    private val userRepository: UserRepository
) {

    fun login(form: LoginForm, request: HttpServletRequest): User =
        authService.login(request, form.uid, form.password)

    // 유저 생성 후 로그인까지
    @Transactional
    fun join(form: JoinForm, request: HttpServletRequest): User {
        if (!form.checkPassword()) throw UserException(PASSWORD_MISMATCH)
        if (userRepository.existsByUid(form.uid)) throw UserException(DUPLICATE_ID)

        val user = userRepository.save(
            User(
                uid = form.uid,
                password = form.password,
                name = form.name,
                email = form.email
            )
        )

        return login(LoginForm(user.uid, user.password), request)
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
                .map { it.causer }
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
            loginUser.following.add(Follow(causer = loginUser, target = targetUser))
        else throw UserException(ALREADY_FOLLOWED)
        return true
    }

    fun unfollowUser(userId: Long, loginUser: User): Boolean {
        loginUser.following.remove(
            loginUser.following
                .find { it.causer.id == loginUser.id && it.target.id == getUser(userId).id }
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
        // 해당 직종이 존재하는지 확인
        form.interests.forEach {
            if (!occupationService.isExists(it)) throw OccupationException("[$it] $OCCUPATION_NOT_FOUND")
        }
        return loginUser.update(
            interests = form.interests.joinToString(",")
        )
    }

    fun updateProfileImage(image: MultipartFile, loginUser: User): User {
        loginUser.profileImage?.let { fileService.deleteFile(it) }
        return loginUser.update(profileImage = fileService.saveFile(image))
    }

    fun updatePassword(form: PasswordUpdateForm, loginUser: User): Boolean {
        if (!form.checkPassword()) throw UserException(PASSWORD_MISMATCH)
        if (form.currentPassword != loginUser.password) throw UserException(PASSWORD_MISMATCH)
        loginUser.update(form.currentPassword)
        return true
    }

    fun deleteUser(form: UserDeleteForm, loginUser: User): Boolean {
        userRepository.delete(loginUser)
        return true
    }
}