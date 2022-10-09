package diary.capstone.domain.file

import diary.capstone.config.RESOURCE_URL

data class FileResponse(
    var originalName: String,
    var source: String,
    var description: String
) {
    constructor(file: File): this(
        originalName = file.originalName,
        source = RESOURCE_URL + file.savedName,
        description = file.description
    )
}

data class ProfileImageFileResponse(
    var originalName: String,
    var source: String,
) {
    constructor(file: File): this(
        originalName = file.originalName,
        source = RESOURCE_URL + file.savedName,
    )
}