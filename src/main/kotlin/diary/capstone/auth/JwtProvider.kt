package diary.capstone.auth

import diary.capstone.config.TOKEN_VALID_TIME
import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.INVALID_TOKEN
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserRepository
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.xml.bind.DatatypeConverter

@Component
class JwtProvider(
    private val userRepository: UserRepository,
) {
    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    fun createToken(subject: String): String {

        val signatureAlgorithm = SignatureAlgorithm.HS256
        val secretKeyByte = DatatypeConverter.parseBase64Binary(secretKey)
        val signingKey = SecretKeySpec(secretKeyByte, signatureAlgorithm.jcaName)

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer("opponent")
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + TOKEN_VALID_TIME))
            .signWith(signingKey)
            .compact()
    }

    fun extractToken(request: HttpServletRequest): String? =
        request.getHeader("Authorization")
//            ?: request.getHeader("authorization")

    fun getSubject(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
            .build()
            .parseClaimsJws(token)
            .body.subject

    fun getUser(token: String): User? =
        userRepository.findByEmail(getSubject(token))

    // 토큰의 유효성 + 만료일자 확인
    fun validateToken(token: String): Boolean =
        !Jwts.parserBuilder()
            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
            .build()
            .parseClaimsJws(token)
            .body
            .expiration
            .before(Date())
}