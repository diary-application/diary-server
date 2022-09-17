package diary.capstone.domain.file

const val FILE_IS_EMPTY = "파일이 존재하지 않습니다."
const val FILE_NOT_FOUND = "해당 파일을 찾을 수 없습니다."

class FileException(message: String): RuntimeException(message)