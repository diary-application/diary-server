package diary.capstone.domain.occupation

const val OCCUPATION_NOT_FOUND = "해당 직종을 찾을 수 없습니다."
const val DUPLICATE_OCCUPATION = "해당 직종이 이미 존재합니다."

class OccupationException(message: String): RuntimeException(message)