package diary.capstone.exception

import diary.capstone.domain.user.AuthException
import diary.capstone.domain.user.INVALID_TOKEN
import diary.capstone.domain.user.MAIL_AUTH_REQUIRED
import diary.capstone.util.*
import io.jsonwebtoken.JwtException
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    val log = logger()

    // 인증 예외 처리
    @ExceptionHandler(AuthException::class)
    fun loginExceptionHandle(ex: AuthException): ResponseEntity<ErrorResponse> {
        log.warn("[인증 예외] : {}", ex.message)
        return when (ex.message) {
            MAIL_AUTH_REQUIRED -> found(ex)
            INVALID_TOKEN -> forbidden(ex)
            else -> unauthorized(ex)
        }
    }
    
    // 토큰 관련 예외 처리
    @ExceptionHandler(JwtException::class)
    fun tokenExceptionHandle(ex: JwtException): ResponseEntity<ErrorResponse> {
        log.warn("[JWT 예외] : {}", ex.message)
        return forbidden(ex)
    }

    // 검증 예외 처리
    @ExceptionHandler(BindException::class)
    fun validationErrorHandle(ex: BindException): ResponseEntity<Any> {
        log.warn("[{}] : {}, \nerrors = {}", ex.javaClass.simpleName, ex.message, ex.bindingResult.fieldErrors)

        return badRequest(
            ex.bindingResult.fieldErrors
                .map { ErrorResponse(it.field, it.defaultMessage ?: "검증 오류") }
        )
    }

    // 이외 모든 예외 처리
    @ExceptionHandler(Exception::class)
    fun globalErrorHandle(ex: Exception): ResponseEntity<ErrorResponse> {
        log.warn("[{}] handled: {}", ex.javaClass.simpleName, ex.message)
        return badRequest(ex)
    }
}