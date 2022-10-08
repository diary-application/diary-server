package diary.capstone.domain.schedule

import diary.capstone.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScheduleService {

    fun createSchedule(form: ScheduleDto, loginUser: User) =
        loginUser.addSchedule(Schedule(date = form.date, content = form.content))

    fun getSchedules(loginUser: User) =
        loginUser.schedules.map { ScheduleDto(it) }

    fun updateSchedule(scheduleId: Long, form: ScheduleDto, loginUser: User) =
        loginUser.schedules.find { it.id == scheduleId }
            ?.let { it.update(form.date, form.content) }
            ?: run { throw ScheduleException(SCHEDULE_NOT_FOUND) }

    fun deleteSchedule(scheduleId: Long, loginUser: User) =
        loginUser.schedules.let { schedules ->
            schedules.find { it.id == scheduleId }
                ?.let { find -> schedules.remove(find) }
                ?: run { throw ScheduleException(SCHEDULE_NOT_FOUND) }
        }
}