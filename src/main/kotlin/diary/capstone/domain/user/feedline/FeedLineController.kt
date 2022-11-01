package diary.capstone.domain.user.feedline

import diary.capstone.auth.Auth
import diary.capstone.domain.user.User
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.validation.Valid

@ApiOperation("피드라인 관련 API")
@Auth
@RestController
@RequestMapping("/user")
class FeedLineController(private val feedLineService: FeedLineService) {

    @ApiOperation(value = "내 피드라인 목록 조회")
    @GetMapping("/feedline")
    fun getMyFeedLines(@ApiIgnore user: User) = user.feedLines.map { FeedLineResponse(it) }

    @ApiOperation(value = "피드라인 생성")
    @PostMapping("/feedline")
    fun createFeedLine(@Valid @RequestBody form: FeedLineRequestForm, @ApiIgnore user: User) =
        feedLineService.createFeedLine(form, user)

    @ApiOperation(value = "피드라인 수정")
    @PutMapping("/feedline/{feedlineId}")
    fun updateFeedLine(
        @PathVariable("feedlineId") feedLineId: Long,
        @Valid @RequestBody form: FeedLineRequestForm,
        @ApiIgnore user: User,
    ) = feedLineService.updateFeedLine(feedLineId, form, user)

    @ApiOperation(value = "피드라인 삭제")
    @DeleteMapping("/feedline/{feedlineId}")
    fun deleteFeedLine(
        @PathVariable("feedlineId") feedLineId: Long,
        @ApiIgnore user: User
    ) = feedLineService.deleteFeedLine(feedLineId, user)
}