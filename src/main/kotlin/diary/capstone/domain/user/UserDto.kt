package diary.capstone.domain.user

import diary.capstone.domain.feedline.FeedLine
import diary.capstone.domain.file.ProfileImageFileResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class LoginForm(
    @field:NotBlank
    var uid: String,

    @field:NotBlank
    var password: String
)

data class MailAuthLoginForm(
    @field:NotBlank
    var uid: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var code: String
)

// 해당 메일에 대한 인증 요청
data class AuthMailForm(@field:NotBlank @field:Email var email: String)

// 해당 메일에 대한 인증 코드와 함께 요청
data class AuthCodeForm(@field:NotBlank @field:Email var email: String, @field:NotBlank var code: String)

data class JoinForm(
    @field:NotBlank
    var uid: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var passwordCheck: String,

    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var name: String
) {
    fun checkPassword(): Boolean = this.password == this.passwordCheck
}

data class MailAuthJoinForm(
    @field:NotBlank
    var uid: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var passwordCheck: String,

    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var code: String,

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
    var name: String,

    @field:NotBlank
    @field:Email
    var email: String,
)

data class UserOccupationUpdateForm(
    @field:NotBlank
    var occupation: String
)

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

data class FeedLineResponse(
    var id: Long,
    var title: String
) {
    constructor(feedLine: FeedLine): this(
        id = feedLine.id!!,
        title = feedLine.title
    )
}

data class UserDetailResponse(
    var id: Long,
    var image: ProfileImageFileResponse?,
    var name: String,
    var email: String,
    var occupation: String?,
    var interests: List<String>,
    var followingCount: Int,
    var followerCount: Int,
    var ip: String
) {
    constructor(user: User): this(
        id = user.id!!,
        image = user.profileImage?.let { ProfileImageFileResponse(it) },
        name = user.name,
        email = user.email,
        occupation = user.occupation?.name,
        interests = user.getInterests(),
        followingCount = user.following.count(),
        followerCount = user.follower.count(),
        ip = user.ip
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