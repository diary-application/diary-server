package diary.capstone.domain.file

data class FileResponse(
    var id: Long,
    var originalName: String,
    var source: String,
    var description: String
) {
    constructor(file: File): this(
        id = file.id!!,
        originalName = file.originalName,
        source = file.source,
        description = file.description
    )
}

data class ProfileImageFileResponse(
    var id: Long,
    var originalName: String,
    var source: String,
) {
    constructor(file: File): this(
        id = file.id!!,
        originalName = file.originalName,
        source = file.source,
    )
}