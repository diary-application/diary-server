package diary.capstone.domain.notice

import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
import diary.capstone.util.getPagedObject
import org.springframework.data.domain.Pageable
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener

@Service
@Transactional
class NoticeService(
    private val sendingOperations: SimpMessageSendingOperations,
    private val userService: UserService,
    private val noticeRepository: NoticeRepository
) {

    @Async
    @TransactionalEventListener
    fun sendNotice(noticeRequest: NoticeRequest) {
        userService.getUserById(noticeRequest.receiver).let {
            it.addNotice(
                noticeRepository.save(
                    Notice(
                        receiver = it,
                        type = noticeRequest.type,
                        content = noticeRequest.content
                    )
                )
            )
        }
        sendingOperations.convertAndSend("/sub/notice/${noticeRequest.receiver}", noticeRequest)
    }

    @Async
    @TransactionalEventListener
    fun sendNotices(notices: List<NoticeRequest>) =
        notices.forEach { sendNotice(it) }

    @Transactional
    fun getAllNotifications(pageable: Pageable, loginUser: User) =
        loginUser.notices.let { notices ->
            if (notices.lastIndex > 0) { notices.last().isRead = true }
            getPagedObject(pageable, notices.sortedByDescending { it.id })
        }

    fun deleteNotice(noticeId: Long, loginUser: User) =
        loginUser.notices.remove(loginUser.notices.find { it.id == noticeId })
}