package diary.capstone.domain.user

import diary.capstone.domain.feed.FeedSimpleResponse
import diary.capstone.domain.file.FileResponse
import org.springframework.data.domain.Page
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class LoginForm(
    @field:NotBlank
    var uid: String,

    @field:NotBlank
    var password: String
)

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

    @field:NotBlank
    var job: String,

    @field:NotBlank
    var category: String
)

data class UserDeleteForm(
    var password: String
)

data class UserSimpleResponse(
    var id: Long,
    var name: String,
    var image: FileResponse?
) {
    constructor(user: User): this(
        id = user.id!!,
        name = user.name,
        image = user.profileImage?.let { FileResponse(it) }
    )
}

data class MyDetailResponse(
    var id: Long,
    var image: FileResponse?,
    var uid: String,
    var name: String,
    var email: String,
    var job: String,
    var category: String,
    var followingCount: Int,
    var followerCount: Int
) {
    constructor(user: User): this(
        id = user.id!!,
        image = user.profileImage?.let { FileResponse(it) },
        uid = user.uid,
        name = user.name,
        email = user.email,
        job = user.job,
        category = user.category,
        followingCount = user.following.count(),
        followerCount = user.follower.count()
    )
}

data class UserDetailResponse(
    var id: Long,
    var image: FileResponse?,
    var name: String,
    var job: String,
    var category: String,
    var followingCount: Int,
    var followerCount: Int
) {
    constructor(user: User): this(
        id = user.id!!,
        image = user.profileImage?.let { FileResponse(it) },
        name = user.name,
        job = user.job,
        category = user.category,
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