package diary.capstone.domain.report

data class ReportDto(
    var type: String,
    var reason: String,
) {
    fun getTypeAndId(): Pair<String, Long> =
        this.type.split("/").let {
            Pair(it[0], it[1].toLong())
        }
}