package diary.capstone.domain.user

// 로그인
const val LOGIN_FAILED = "이메일/비밀번호를 다시 확인해주세요."
const val MAIL_AUTH_REQUIRED = "메일 인증이 필요합니다."
const val AUTH_CODE_MISMATCH = "인증 코드가 일치하지 않습니다."

// 회원가입
const val PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다."
const val DUPLICATE_EMAIL = "이미 사용중인 이메일입니다."

// 인증
const val NOT_LOGIN_USER = "로그인 후 이용 가능합니다."
const val ADMIN_ONLY = "관리자만 이용 가능합니다."
const val INVALID_TOKEN = "유효하지 않은 토큰입니다."

// 유저
const val USER_NOT_FOUND = "해당 유저를 찾을 수 없습니다."
const val ALREADY_FOLLOWED = "해당 유저를 이미 팔로잉 중입니다."
const val FOLLOW_TARGET_INVALID = "팔로우 할 수 없는 유저입니다."

// 비밀번호 변경
const val CURRENT_PASSWORD_MISMATCH = "현재 비밀번호가 일치하지 않습니다."
const val NEW_PASSWORD_MISMATCH = "새 비밀번호가 일치하지 않습니다."

class UserException(message: String): RuntimeException(message)
class AuthException(message: String): Exception(message)