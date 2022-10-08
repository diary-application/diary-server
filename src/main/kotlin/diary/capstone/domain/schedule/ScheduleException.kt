package diary.capstone.domain.schedule

const val SCHEDULE_NOT_FOUND = "해당 일정을 찾을 수 없습니다."

class ScheduleException(message: String): RuntimeException(message)