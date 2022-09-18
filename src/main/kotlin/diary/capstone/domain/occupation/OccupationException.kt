package diary.capstone.domain.occupation

import diary.capstone.config.INTERESTS_LIMIT

const val OCCUPATION_NOT_FOUND = "해당 직종을 찾을 수 없습니다."
const val DUPLICATE_OCCUPATION = "해당 직종이 이미 존재합니다."
const val INTERESTS_EXCEEDED = "관심 분야는 최대 ${INTERESTS_LIMIT}개까지 설정 가능합니다."

class OccupationException(message: String): RuntimeException(message)