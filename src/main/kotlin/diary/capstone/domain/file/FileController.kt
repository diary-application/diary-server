package diary.capstone.domain.file

import diary.capstone.auth.Auth
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Auth
@RestController("/file")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun uploadFile(@ModelAttribute form: FileRequest): FileResponse =
        FileResponse(fileService.uploadFile(form.file))
}