package diary.capstone.domain.user

import diary.capstone.domain.feedline.FeedLine
import diary.capstone.domain.file.ProfileImageFileResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class LoginForm(
    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var password: String
)

data class MailAuthLoginForm(
    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var code: String
)

// 해당 메일에 대한 인증 요청
data class AuthMailForm(@field:NotBlank @field:Email var email: String)

// 해당 메일에 대한 인증 코드와 함께 요청
data class AuthCodeForm(@field:NotBlank @field:Email var email: String, @field:NotBlank var code: String)

// 로그인 성공 시 토큰 응답
data class TokenResponse(var token: String)

data class JoinForm(
    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var passwordCheck: String,

    @field:NotBlank
    var name: String
) {
    fun checkPassword(): Boolean = this.password == this.passwordCheck
}

data class PasswordUpdateForm(
    var currentPassword: String,
    var newPassword: String,
    var newPasswordCheck: String
) {
    fun checkPassword(): Boolean = this.newPassword == this.newPasswordCheck
}

data class UserInfoUpdateForm(
    @field:NotBlank
    var name: String
)

data class UserMessageUpdateForm(
    var message: String
)

data class UserOccupationUpdateForm(@field:NotBlank var occupation: String)

data class UserInterestsUpdateForm(var interests: List<String>)

data class UserDeleteForm(var password: String)

data class UserSimpleResponse(
    var id: Long,
    var name: String,
    var image: ProfileImageFileResponse?
) {
    constructor(user: User): this(
        id = user.id!!,
        name = user.name,
        image = user.profileImage?.let { ProfileImageFileResponse(it) }
    )
}

data class FeedLineResponse(var id: Long, var title: String) {
    constructor(feedLine: FeedLine): this(
        id = feedLine.id!!,
        title = feedLine.title
    )
}

data class UserDetailResponse(
    var id: Long,
    var image: ProfileImageFileResponse?,
    var name: String,
    var message: String,
    var email: String,
    var occupation: String?,
    var interests: List<String>,
    var followingCount: Int,
    var followerCount: Int
) {
    constructor(user: User): this(
        id = user.id!!,
        image = user.profileImage?.let { ProfileImageFileResponse(it) },
        name = user.name,
        message = user.message,
        email = user.email,
        occupation = user.occupation?.name,
        interests = user.getInterests(),
        followingCount = user.following.count(),
        followerCount = user.follower.count()
    )
}

data class UserPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var users: List<UserSimpleResponse>
) {
    constructor(users: Page<User>): this(
        currentPage = users.number + 1,
        totalPages = users.totalPages,
        totalElements = users.totalElements,
        users = users.content
            .map { UserSimpleResponse(it) }
            .toList()
    )
}