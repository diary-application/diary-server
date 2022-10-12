package diary.capstone.domain.occupation

import diary.capstone.auth.Admin
import diary.capstone.auth.Auth
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@ApiOperation("직종 관련 API")
@Auth
@RestController
@RequestMapping("/occupation", produces = ["application/json"])
class OccupationController(private val occupationService: OccupationService) {

    @ApiOperation(value = "직종 목록 조회")
    @GetMapping
    fun getOccupations(): List<String> = occupationService.getOccupations()
    
    @ApiOperation(value = "[관리자] 직종 생성")
    @Admin
    @PostMapping
    fun createOccupation(@Valid @RequestBody form: OccupationRequestForm) =
        occupationService.createOccupation(form.name)

    @ApiOperation(value = "[관리자] 직종 삭제")
    @Admin
    @DeleteMapping
    fun deleteOccupation(@RequestParam("name") name: String) =
        occupationService.deleteOccupation(name)
}