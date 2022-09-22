package diary.capstone.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class SuccessResponse(val result: String)

data class BoolResponse(val result: Boolean = true)

data class ErrorResponse(val cause: String = "", val message: String = "")

fun ok(body: String) =
    ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse(body))

fun badRequest(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))

fun badRequest(body: Any) =
    ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(body)

fun unauthorized(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))

fun proxyAuthenticationRequired(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))
