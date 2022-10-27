package diary.capstone.domain.feed

import diary.capstone.auth.Auth
import diary.capstone.config.FEED_PAGE_SIZE
import diary.capstone.domain.feed.comment.CommentPagedResponse
import diary.capstone.domain.feed.comment.CommentRequestForm
import diary.capstone.domain.feed.comment.CommentResponse
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserSimpleResponse
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.validation.Valid

@ApiOperation("피드 관련 API")
@Auth
@RestController
@RequestMapping("/feed")
class FeedController(private val feedService: FeedService) {

    @ApiOperation(
        value = "피드 생성",
        notes = "images 와 descriptions 의 수는 같게 하여 요청\n " +
                "한 이미지에 설명글이 없을 경우 해당 images 의 descriptions 는 빈 문자열로 요청"
    )
    @PostMapping
    fun createFeed(@Valid @ModelAttribute form: FeedRequestForm, @ApiIgnore user: User) =
        FeedSimpleResponse(feedService.createFeed(form, user), user)

    @ApiOperation(
        value = "피드 목록 조회",
        notes = "각 파라미터는 한 요청 시 하나만 요청 가능\n " +
                "* userid: 해당 유저가 작성한 피드 목록 조회\n " +
                "* feedlineid: 해당 피드라인으로 피드 목록 조회\n "
    )
    @GetMapping
    fun getFeeds(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC, size = FEED_PAGE_SIZE) pageable: Pageable,
        @RequestParam(name = "userid", required = false) userId: Long?,
        @RequestParam(name = "feedlineid", required = false) feedLineId: Long?,
        @ApiIgnore user: User
    ) = FeedPagedResponse(feedService.getFeeds(pageable, userId, feedLineId, user), user)

    @ApiOperation(value = "피드 상세 조회")
    @GetMapping("/{feedId}")
    fun getFeed(@PathVariable("feedId") feedId: Long, @ApiIgnore user: User) =
        FeedDetailResponse(feedService.getFeed(feedId), user)

    @ApiOperation(value = "피드 수정")
    @PatchMapping("/{feedId}")
    fun updateFeed(@PathVariable("feedId") feedId: Long,
                   @Valid @ModelAttribute form: FeedRequestForm,
                   @ApiIgnore user: User
    ) = FeedDetailResponse(feedService.updateFeed(feedId, form, user), user)

    @ApiOperation(value = "피드 삭제")
    @DeleteMapping("/{feedId}")
    fun deleteFeed(@PathVariable("feedId") feedId: Long, @ApiIgnore user: User) =
        feedService.deleteFeed(feedId, user)
}

@ApiOperation("댓글 관련 API")
@Auth
@RestController
@RequestMapping("/feed/{feedId}/comment")
class CommentController(private val feedService: FeedService) {

    @ApiOperation(value = "루트 댓글 생성")
    @PostMapping
    fun createRootComment(@PathVariable("feedId") feedId: Long,
                          @Valid @RequestBody form: CommentRequestForm,
                          @ApiIgnore user: User
    ) = feedService.createRootComment(feedId, form, user)

    @ApiOperation(value = "대댓글 생성")
    @PostMapping("/{commentId}")
    fun createChildComment(@PathVariable("feedId") feedId: Long,
                           @PathVariable("commentId") commentId: Long,
                           @Valid @RequestBody form: CommentRequestForm,
                           @ApiIgnore user: User
    ) = feedService.createChildComment(feedId, commentId, form, user)

    @ApiOperation(
        value = "해당 피드의 루트 댓글만 조회", 
        notes = "user 파라미터가 me 일 경우 내 루트 댓글만 조회\n " +
                "user 파라미터를 포함하지 않을 경우 다른 유저들의 루트 댓글 조회"
    )
    @GetMapping
    fun getRootComments(@PathVariable("feedId") feedId: Long,
                        @RequestParam(name = "user", required = false) user: String?,
                        @PageableDefault pageable: Pageable,
                        @ApiIgnore loginUser: User
    ) = CommentPagedResponse(
            when (user) {
                "me" -> feedService.getMyComments(feedId, pageable, loginUser)
                else -> feedService.getRootComments(feedId, pageable, loginUser)
            }, loginUser
        )

    @ApiOperation(value = "해당 댓글의 대댓글 목록 조회")
    @GetMapping("/{commentId}")
    fun getChildComments(@PathVariable("feedId") feedId: Long,
                         @PathVariable("commentId") commentId: Long,
                         @PageableDefault pageable: Pageable,
                         @ApiIgnore user: User
    ) = CommentPagedResponse(feedService.getChildComments(feedId, commentId, pageable), user)

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{commentId}")
    fun updateComment(@PathVariable("feedId") feedId: Long,
                      @PathVariable("commentId") commentId: Long,
                      @Valid @RequestBody form: CommentRequestForm,
                      @ApiIgnore user: User
    ) = CommentResponse(feedService.updateComment(feedId, commentId, form, user), user)

    @ApiOperation(value = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable("feedId") feedId: Long,
                      @PathVariable("commentId") commentId: Long,
                      @ApiIgnore user: User
    ) = feedService.deleteComment(feedId, commentId, user)
}

@ApiOperation("피드 좋아요 관련 API")
@Auth
@RestController
@RequestMapping("/feed/{feedId}/like")
class FeedLikeController(private val feedService: FeedService) {

    @ApiOperation(value = "해당 피드를 좋아요한 유저 목록")
    @GetMapping
    fun getFeedLikeUsers(
        @PageableDefault pageable: Pageable,
        @PathVariable("feedId") feedId: Long,
        @ApiIgnore user: User
    ) = feedService.getFeedLikes(pageable, feedId).map { UserSimpleResponse(it, user) }

    @ApiOperation(value = "해당 피드 좋아요 등록")
    @PostMapping
    fun likeFeed(@PathVariable("feedId") feedId: Long, @ApiIgnore user: User) =
        FeedDetailResponse(feedService.likeFeed(feedId, user), user)

    @ApiOperation(value = "해당 피드 좋아요 취소")
    @DeleteMapping
    fun cancelLikeFeed(@PathVariable("feedId") feedId: Long, @ApiIgnore user: User) =
        FeedDetailResponse(feedService.cancelLikeFeed(feedId, user), user)
}

@ApiOperation("댓글 좋아요 관런 API")
@Auth
@RestController
@RequestMapping("/feed/{feedId}/comment/{commentId}/like")
class CommentLikeController(private val feedService: FeedService) {

    @ApiOperation(value = "해당 댓글 좋아요 등록")
    @PostMapping
    fun likeComment(
        @PathVariable("feedId") feedId: Long,
        @PathVariable("commentId") commentId: Long,
        @ApiIgnore user: User
    ) = CommentResponse(feedService.likeComment(feedId, commentId, user), user)

    @ApiOperation(value = "해당 댓글 좋아요 취소")
    @DeleteMapping
    fun cancelLikeComment(
        @PathVariable("feedId") feedId: Long,
        @PathVariable("commentId") commentId: Long,
        @ApiIgnore user: User
    ) = CommentResponse(feedService.cancelLikeComment(feedId, commentId, user), user)
}