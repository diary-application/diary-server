package diary.capstone.feed

import diary.capstone.user.User
import diary.capstone.user.UserService
import diary.capstone.util.filterChildComments
import diary.capstone.util.filterMyComments
import diary.capstone.util.filterNotMyComments
import diary.capstone.util.filterRootComments
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Service
@Transactional
class FeedService(
    private val feedRepository: FeedRepository,
    private val userService: UserService
) {
    // 피드 작성
    fun createFeed(form: FeedRequestForm, loginUser: User): Feed =
        feedRepository.save(
            Feed(
                writer = loginUser,
                content = form.content
            )
        )

    // 피드 목록 검색 (페이징)
    @Transactional(readOnly = true)
    fun getFeeds(pageable: Pageable, userId: Long?): Page<Feed> {
        // 해당 유저의 피드 목록 조회
        userId?.let {
            val feeds = userService.getUser(userId).feeds.sortedByDescending { it.id }
            val total = feeds.size
            val start = pageable.offset.toInt()
            val end = min((start + pageable.pageSize), total)
            return PageImpl(feeds.subList(start, end), pageable, total.toLong())
        } ?:
        // 모든 피드 조회
        run { return feedRepository.findAll(pageable) }
        // TODO 조건 별로 조회 추가
    }

    @Transactional(readOnly = true)
    fun getFeed(feedId: Long): Feed =
        feedRepository.findById(feedId).orElseThrow { throw FeedException(FEED_NOT_FOUND) }

    // 피드 좋아요 등록
    fun likeFeed(feedId: Long, loginUser: User): Boolean {
        val feed = getFeed(feedId)
        if (feed.likes.none { it.user == loginUser })
            feed.likes.add(FeedLike(feed = feed, user = loginUser))
        else
            throw FeedLikeException(ALREADY_LIKED_FEED)
        return true
    }

    // 피드 좋아요 취소
    fun cancelLikeFeed(feedId: Long, loginUser: User): Boolean {
        val feed = getFeed(feedId)
        feed.likes.remove(
            feed.likes
                .find { it.user.id == loginUser.id }
        )
        return true
    }

    fun updateFeed(feedId: Long, form: FeedRequestForm, loginUser: User): Feed {
        val feed = getFeed(feedId)
        feedPermissionCheck(feed, loginUser)
        feed.update(form.content)
        return feed
    }

    fun deleteFeed(feedId: Long, loginUser: User): Boolean {
        val feed = getFeed(feedId)
        feedPermissionCheck(feed, loginUser)
        feedRepository.delete(feed)
        return true
    }

    // 피드 접근 권한 체크
    private fun feedPermissionCheck(feed: Feed, loginUser: User) {
        if (feed.writer.id != loginUser.id) throw FeedException(FEED_ACCESS_DENIED)
    }

    // 댓글 리스트를 Page 객체에 담아서 반환
    private fun getPagedComments(pageable: Pageable, comments: List<Comment>): Page<Comment> {
        val total = comments.size
        val start = pageable.offset.toInt()
        val end = min((start + pageable.pageSize), total)
        return PageImpl(comments.subList(start, end), pageable, total.toLong())
    }

    private fun getComment(feedId: Long, commentId: Long): Comment =
        getFeed(feedId).comments
            .find { it.id == commentId } ?: throw CommentException(COMMENT_NOT_FOUND)

    // 새 루트 댓글 생성
    fun createRootComment(feedId: Long, form: CommentRequestForm, loginUser: User) {
        val feed = getFeed(feedId)
        val comment = Comment(
            feed = feed,
            writer = loginUser,
            content = form.content
        )
        feed.comments.add(comment)
    }

    // 대댓글 생성
    fun createChildComment(feedId: Long, parentId: Long, form: CommentRequestForm, loginUser: User) {
        val feed = getFeed(feedId)
        val parentComment = getComment(feedId, parentId)
        val comment = Comment(
            feed = feed,
            writer = loginUser,
            content = form.content,
            parent = parentComment
        )
        parentComment.children.add(comment)
    }

    /**
     * 댓글 페이징 조회
     * id 오름차순으로 정렬 (등록일이 빠른 댓글이 위로)
     */
    // 모든 루트 댓글 조회 (부모 댓글 x, 로그인한 유저가 쓴 댓글 제외)
    @Transactional(readOnly = true)
    fun getRootComments(feedId: Long, pageable: Pageable, loginUser: User): Page<Comment> =
        getPagedComments(pageable,
            getFeed(feedId).comments
                .filterRootComments()
                .filterNotMyComments(loginUser.id!!)
                .sortedBy { it.id }
        )

    // 해당 피드의 내가 쓴 루트 댓글만 조회 (부모 댓글 x, 로그인한 유저가 쓴 댓글만)
    @Transactional(readOnly = true)
    fun getMyComments(feedId: Long, pageable: Pageable, loginUser: User): Page<Comment> =
        getPagedComments(pageable,
            getFeed(feedId).comments
                .filterRootComments()
                .filterMyComments(loginUser.id!!)
                .sortedBy { it.id }
        )

    // 해당 댓글의 대댓글들 조회 (해당 댓글의 자식 댓글들만)
    @Transactional(readOnly = true)
    fun getChildComments(feedId: Long, commentId: Long, pageable: Pageable): Page<Comment> =
        getPagedComments(pageable,
            getFeed(feedId).comments
                .filterChildComments(commentId)
                .sortedBy { it.id }
        )

    fun updateComment(feedId: Long, commentId: Long, form: CommentRequestForm, loginUser: User): Comment {
        val comment = getComment(feedId, commentId)
        commentPermissionCheck(comment, loginUser)
        comment.update(form.content)
        return comment
    }

    fun deleteComment(feedId: Long, commentId: Long, loginUser: User): Boolean {
        val feed = getFeed(feedId)
        val comment = getComment(feedId, commentId)
        commentPermissionCheck(comment, loginUser)
        feed.comments.remove(comment)
        return true
    }

    // 댓글 수정/삭제 시 권한 체크
    private fun commentPermissionCheck(comment: Comment, loginUser: User) {
        if (comment.writer.id != loginUser.id) throw CommentException(COMMENT_ACCESS_DENIED)
    }
}