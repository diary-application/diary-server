package diary.capstone.domain.occupation

import diary.capstone.auth.Admin
import diary.capstone.auth.Auth
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@ApiOperation("직종 관련 API")
@Auth
@RestController
@RequestMapping("/occupation")
class OccupationController(private val occupationService: OccupationService) {

    @ApiOperation(value = "직종 목록 조회")
    @GetMapping
    fun getOccupations(): List<OccupationResponse> =
        occupationService.getOccupations().map { OccupationResponse(it.id!!, it.name) }
    
    @ApiOperation(value = "[관리자] 직종 생성")
    @Admin
    @PostMapping
    fun createOccupation(@Valid @RequestBody form: OccupationRequestForm) =
        occupationService.createOccupation(form.name)

    @ApiOperation(value = "[관리자] 직종 수정")
    @Admin
    @PutMapping("/{occupationId}")
    fun updateOccupation(
        @PathVariable("occupationId") occupationId: Long,
        @RequestBody form: OccupationRequestForm
    ) = occupationService.updateOccupation(occupationId, form)

    @ApiOperation(value = "[관리자] 직종 삭제")
    @Admin
    @DeleteMapping
    fun deleteOccupation(@RequestParam("name") name: String) =
        occupationService.deleteOccupation(name)
}