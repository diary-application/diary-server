package diary.capstone.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorResponse(val cause: String = "", val message: String = "")

fun created(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))

fun found(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.FOUND)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))

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

fun forbidden(ex: Exception) =
    ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))