package diary.capstone.domain.file

data class FileResponse(
    var originalName: String,
    var source: String,
    var description: String
) {
    constructor(file: File): this(
        originalName = file.originalName,
        source = file.source,
        description = file.description
    )
}

data class ProfileImageFileResponse(
    var originalName: String,
    var source: String,
) {
    constructor(file: File): this(
        originalName = file.originalName,
        source = file.source,
    )
}