package diary.capstone.domain.setting

import org.springframework.data.jpa.repository.JpaRepository

interface SettingRepository: JpaRepository<Setting, Long> {

    fun findByUser(userId: Long): Setting?
}