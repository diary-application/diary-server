package diary.capstone.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import javax.servlet.http.HttpServletRequest
import kotlin.math.min

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

/**
 * IP 추출 메소드
 */
fun HttpServletRequest.getIp(): String =
    this.getHeader("X-Forwarded-For")
        ?: this.getHeader("Proxy-Client-IP")
        ?: this.getHeader("WL-Proxy-Client-IP")
        ?: this.getHeader("HTTP_CLIENT_IP")
        ?: this.getHeader("HTTP_X_FORWARDED_FOR")
        ?: this.remoteAddr

/**
 * Pageable, 엔티티 객체의 목록을 받아
 * Paged<엔티티> 를 반환해주는 메소드
 */
fun <T> getPagedObject(pageable: Pageable, entityList: List<T>): Page<T> {
    val total = entityList.size
    val start = pageable.offset.toInt()
    val end = min((start + pageable.pageSize), total)
    return PageImpl(entityList.subList(start, end), pageable, total.toLong())
}