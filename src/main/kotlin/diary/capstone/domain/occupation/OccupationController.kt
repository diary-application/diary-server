package diary.capstone.domain.occupation

import diary.capstone.auth.Admin
import diary.capstone.auth.Auth
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Auth
@RestController
@RequestMapping("/occupation", produces = ["application/json"])
class OccupationController(private val occupationService: OccupationService) {

    // 직종 이름 목록 반환
    @GetMapping
    fun getOccupations(): List<String> = occupationService.getOccupations()
    
    // 직종 생성, 삭제는 관리자만 가능
    @Admin
    @PostMapping
    fun createOccupation(@Valid @RequestBody form: OccupationRequestForm) =
        occupationService.createOccupation(form.name)

    @Admin
    @DeleteMapping
    fun deleteOccupation(@RequestParam("name") name: String) =
        occupationService.deleteOccupation(name)
}