package diary.capstone.domain.file

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
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
import javax.annotation.PostConstruct

@Service
@Transactional
class FileService(private val fileRepository: FileRepository) {
    private lateinit var s3Client: AmazonS3

    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucketName: String

    @Value("\${cloud.aws.credentials.accessKey}")
    private lateinit var accessKey: String

    @Value("\${cloud.aws.credentials.secretKey}")
    private lateinit var secretKey: String

    @Value("\${cloud.aws.region.static}")
    private lateinit var region: String

    @PostConstruct
    fun setS3Client() {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build()
    }

    // 서버에 파일 업로드 & DB에 파일 저장
    fun saveFile(file: MultipartFile, description: String = ""): File {
        try {
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentType = file.contentType
            objectMetadata.contentLength = file.size
            val originalName = file.originalFilename.toString()
            val ext = originalName.substring(originalName.lastIndexOf(".") + 1)
            val source = UUID.randomUUID().toString() + "." + ext

            // S3 에 파일 업로드
            s3Client.putObject(
                PutObjectRequest(this.bucketName, source, file.inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
            logger().info("$originalName Uploaded: {}", source)

            return fileRepository.save(
                File(
                    originalName = originalName,
                    source = s3Client.getUrl(this.bucketName, source).toString(),
                    description = description
                )
            )
        } catch (e: Exception) {
            logger().warn(e.stackTraceToString())
            throw FileException(e.message ?: "Amazon S3 객체 업로드 오류")
        }
    }

    fun deleteFile(file: File) {
        s3Client.deleteObject(
            DeleteObjectRequest(
                this.bucketName,
                file.source.substring(file.source.lastIndexOf('/') + 1)
            )
        )
        fileRepository.delete(file)
    }
}