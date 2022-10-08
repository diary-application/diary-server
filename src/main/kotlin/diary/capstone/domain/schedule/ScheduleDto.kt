package diary.capstone.domain.schedule

data class ScheduleDto(
    var date: String,
    var content: String
) {
    constructor(schedule: Schedule): this(
        date = schedule.date,
        content = schedule.content
    )
}