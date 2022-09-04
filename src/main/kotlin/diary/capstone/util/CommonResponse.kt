package diary.capstone.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

data class BoolResponse(val result: Boolean = true)

data class ErrorResponse(val cause: String = "", val message: String = "")

data class ErrorListResponse(val errors: List<ErrorResponse>)

fun convertJson(error: FieldError) = ErrorResponse(VALIDATION_ERROR, error.field + " / " + error.defaultMessage)

fun convertJson(errors: List<FieldError>): ErrorListResponse {
    val result = mutableListOf<ErrorResponse>()
    errors.forEach { result.add(convertJson(it)) }
    return ErrorListResponse(result)
}

fun badRequest(ex: Exception) =
    ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))

fun badRequest(bindingResult: BindingResult) =
    ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(convertJson(bindingResult.fieldErrors))

fun unauthorized(ex: Exception) =
    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))
