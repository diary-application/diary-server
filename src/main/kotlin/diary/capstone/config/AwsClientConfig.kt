package diary.capstone.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.accessKey}") val accessKey: String,
    @Value("\${cloud.aws.credentials.secretKey}") val secretKey: String,
    @Value("\${cloud.aws.region.static}") val region: String
) {
    @Bean
    fun s3Client(): AmazonS3 = AmazonS3ClientBuilder.standard()
        .withCredentials(
            AWSStaticCredentialsProvider(
                BasicAWSCredentials(accessKey, secretKey)
            )
        )
        .withRegion(region)
        .build()
}