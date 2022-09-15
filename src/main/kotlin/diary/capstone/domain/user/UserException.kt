package diary.capstone.domain.user

// 로그인
const val LOGIN_FAILED = "아이디/비밀번호를 다시 확인해주세요."

// 회원가입
const val PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다."
const val DUPLICATE_ID = "해당 아이디가 이미 존재합니다."

// 인증
const val NOT_LOGIN_USER = "로그인 후 이용 가능합니다."

// 유저
const val USER_NOT_FOUND = "해당 유저를 찾을 수 없습니다."
const val ALREADY_FOLLOWED = "해당 유저를 이미 팔로잉 중입니다."
const val FOLLOW_TARGET_INVALID = "팔로우 할 수 없는 유저입니다."

class UserException(message: String): RuntimeException(message)
class AuthException(message: String): RuntimeException(message)