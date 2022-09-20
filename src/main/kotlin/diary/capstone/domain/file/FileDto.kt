package diary.capstone.domain.file

data class FileResponse(
    var originalName: String,
    var savedName: String,
    var description: String
) {
    constructor(file: File): this(
        originalName = file.originalName,
        savedName = file.savedName,
        description = file.description
    )
}

data class ProfileImageFileResponse(
    var originalName: String,
    var savedName: String,
) {
    constructor(file: File): this(
        originalName = file.originalName,
        savedName = file.savedName,
    )
}