package diary.capstone.domain.file

import diary.capstone.config.FILE_SAVE_PATH
import diary.capstone.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class FileService(private val fileRepository: FileRepository) {
    var log = logger()

    // 서버에 파일 업로드 & DB에 파일 저장
    fun saveFile(file: MultipartFile, description: String = ""): File {
        try {
            val originalName = file.originalFilename.toString()
            val savedName = UUID.randomUUID().toString() + ".${originalName.split(".")[1]}"

            // 서버 스토리지의 설정된 경로에 MultipartFile 저장
            file.transferTo(java.io.File(FILE_SAVE_PATH + savedName))

            return fileRepository.save(
                File(
                    originalName = originalName,
                    savedName = savedName,
                    description = description
                )
            )
        } catch (e: Exception) {
            log.warn(e.stackTraceToString())
            throw FileException("${e.message ?: "알 수 없는 오류"}")
        }
    }

    fun deleteFile(file: File) = fileRepository.delete(file)
}