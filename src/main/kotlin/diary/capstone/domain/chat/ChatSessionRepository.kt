package diary.capstone.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatSessionRepository: JpaRepository<ChatSession, Long> {}

interface ChatRepository: JpaRepository<Chat, Long> {}