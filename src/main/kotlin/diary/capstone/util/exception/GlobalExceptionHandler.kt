package diary.capstone.util.exception

import diary.capstone.user.AuthException
import diary.capstone.util.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    val log = logger()

    @ExceptionHandler(AuthException::class)
    fun loginExceptionHandle(ex: AuthException): ResponseEntity<ErrorResponse> {
        log.warn("[LoginException] : {}", ex.message)
        return unauthorized(ex)
    }

    @ExceptionHandler(ValidationException::class)
    fun validationErrorHandle(ex: ValidationException): ResponseEntity<ErrorListResponse> {
        log.warn("[{}] : {}, \nerrors = {}", ex.javaClass.simpleName, ex.message, ex.bindingResult.fieldErrors)
        return badRequest(ex.bindingResult)
    }

    @ExceptionHandler(Exception::class)
    fun globalErrorHandle(ex: Exception): ResponseEntity<ErrorResponse> {
        log.warn("[{}] handled: {}", ex.javaClass.simpleName, ex.message)
        return badRequest(ex)
    }
}