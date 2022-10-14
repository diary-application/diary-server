package diary.capstone.domain.file

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import diary.capstone.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class FileService(
    private val fileRepository: FileRepository,
    private val amazonS3Client: AmazonS3Client
) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var s3Bucket: String

    // 서버에 파일 업로드 & DB에 파일 저장
    fun saveFile(file: MultipartFile, description: String = ""): File {
        try {
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentType = file.contentType
            objectMetadata.contentLength = file.size
            val originalName = file.originalFilename.toString()
            val ext = originalName.substring(originalName.lastIndexOf(".") + 1)
            val source = UUID.randomUUID().toString() + "." + ext

            // 서버 스토리지의 설정된 경로에 MultipartFile 저장
//            file.transferTo(java.io.File(FILE_SAVE_PATH + savedName))
            // S3 에 파일 업로드
            amazonS3Client.putObject(
                PutObjectRequest(this.s3Bucket, source, file.inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )

            return fileRepository.save(
                File(
                    originalName = originalName,
                    source = amazonS3Client.getUrl(this.s3Bucket, source).toString(),
                    description = description
                )
            )
        } catch (e: Exception) {
            logger().warn(e.stackTraceToString())
            throw FileException(e.message ?: "파일 업로드 오류")
        }
    }

    fun deleteFile(file: File) {
        amazonS3Client.deleteObject(
            DeleteObjectRequest(this.s3Bucket, file.source)
        )
        fileRepository.delete(file)
    }
}