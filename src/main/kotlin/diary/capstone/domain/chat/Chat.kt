package diary.capstone.domain.chat

import java.time.LocalDateTime

class Chat(
    var id: String,
    var message: String,
    var sender: String,
    var receiver: String,

    var createTime: LocalDateTime
)