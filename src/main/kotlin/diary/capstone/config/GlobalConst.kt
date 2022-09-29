package diary.capstone.config

// 관심 분야 최대 개수
const val INTERESTS_LIMIT = 3

// 페이지당 표시 할 데이터 개수
const val FEED_PAGE_SIZE = 10
const val COMMENT_PAGE_SIZE = 10

// 정적 리소스 캐싱 시간(분)
const val CACHING_MINUTES: Long = 5

// 인증 코드 설정
const val AUTH_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"  // 인증 코드에 사용할 문자열들
const val AUTH_CODE_DIGITS = 6                                      // 인증 코드의 자릿수
const val AUTH_VALID_MINUTE = 5                                     // 인증 코드 유효 시간(분)