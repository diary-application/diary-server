package diary.capstone.domain.file

import org.springframework.web.multipart.MultipartFile

data class FileRequest(var files: List<MultipartFile>)

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