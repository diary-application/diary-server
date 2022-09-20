package diary.capstone.domain.feed

import diary.capstone.domain.user.User
import diary.capstone.util.BoolResponse
import diary.capstone.auth.Auth
import diary.capstone.domain.feed.comment.CommentPagedResponse
import diary.capstone.domain.feed.comment.CommentRequestForm
import diary.capstone.domain.feed.comment.CommentResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 피드 CRUD 컨트롤러
 */
@Auth
@RestController
@RequestMapping("/feed")
class FeedController(private val feedService: FeedService) {

    // 피드 생성
    @PostMapping
    fun createFeed(@Valid @ModelAttribute form: FeedRequestForm, user: User) =
        FeedSimpleResponse(feedService.createFeed(form, user), user)

    // 피드 목록 조회 (모든 피드, 특정 유저의 피드)
    @GetMapping
    fun getFeeds(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
                 @RequestParam(name = "userId", required = false) userId: Long?,
                 user: User
    ) = FeedPagedResponse(feedService.getFeeds(pageable, userId, user), user)

    // 피드 상세 보기
    @GetMapping("/{feedId}")
    fun getFeed(@PathVariable("feedId") feedId: Long, user: User) =
        FeedDetailResponse(feedService.getFeed(feedId), user)

    // 피드 수정
    @PatchMapping("/{feedId}")
    fun updateFeed(@PathVariable("feedId") feedId: Long,
                   @Valid @ModelAttribute form: FeedRequestForm,
                   user: User
    ) = FeedDetailResponse(feedService.updateFeed(feedId, form, user), user)

    // 피드 삭제
    @DeleteMapping("/{feedId}")
    fun deleteFeed(@PathVariable("feedId") feedId: Long, user: User) =
        BoolResponse(feedService.deleteFeed(feedId, user))
}

/**
 * 댓글 CRUD 컨트롤러
 */
@Auth
@RestController
@RequestMapping("/feed/{feedId}/comment")
class CommentController(private val feedService: FeedService) {

    // 루트 댓글 생성
    @PostMapping
    fun createRootComment(@PathVariable("feedId") feedId: Long,
                          @Valid @RequestBody form: CommentRequestForm,
                          user: User
    ) = feedService.createRootComment(feedId, form, user)

    // 대댓글 생성
    @PostMapping("/{commentId}")
    fun createChildComment(@PathVariable("feedId") feedId: Long,
                           @PathVariable("commentId") commentId: Long,
                           @Valid @RequestBody form: CommentRequestForm,
                           user: User
    ) = feedService.createChildComment(feedId, commentId, form, user)

    // 루트 댓글 목록 조회(내가 쓴 댓글 or 그 외 댓글)
    @GetMapping
    fun getRootComments(@PathVariable("feedId") feedId: Long,
                        @RequestParam(name = "user", required = false) user: String?,
                        @PageableDefault pageable: Pageable,
                        loginUser: User
    ) = CommentPagedResponse(
        when (user) {
            "me" -> feedService.getMyComments(feedId, pageable, loginUser)
            else -> feedService.getRootComments(feedId, pageable, loginUser)
        }
    )

    // 특정 댓글의 대댓글 목록 조회
    @GetMapping("/{commentId}")
    fun getChildComments(@PathVariable("feedId") feedId: Long,
                         @PathVariable("commentId") commentId: Long,
                         @PageableDefault pageable: Pageable
    ) = CommentPagedResponse(feedService.getChildComments(feedId, commentId, pageable))

    // 댓글 수정
    @PutMapping("/{commendId}")
    fun updateComment(@PathVariable("feedId") feedId: Long,
                      @PathVariable("commentId") commentId: Long,
                      @Valid @RequestBody form: CommentRequestForm,
                      user: User
    ) = CommentResponse(feedService.updateComment(feedId, commentId, form, user))

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable("feedId") feedId: Long,
                      @PathVariable("commentId") commentId: Long,
                      user: User
    ) = BoolResponse(feedService.deleteComment(feedId, commentId, user))
}

/**
 * 피드 좋아요 등록/삭제 컨트롤러
 */
@Auth
@RestController
@RequestMapping("/feed/{feedId}/like")
class FeedLikeController(private val feedService: FeedService) {

    @PostMapping
    fun likeFeed(@PathVariable("feedId") feedId: Long, user: User) =
        BoolResponse(feedService.likeFeed(feedId, user))

    @DeleteMapping
    fun cancelLikeFeed(@PathVariable("feedId") feedId: Long, user: User) =
        BoolResponse(feedService.cancelLikeFeed(feedId, user))
}