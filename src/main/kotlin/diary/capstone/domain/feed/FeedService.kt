package diary.capstone.domain.feed

import diary.capstone.domain.feed.comment.Comment
import diary.capstone.domain.feed.comment.CommentRequestForm
import diary.capstone.domain.file.File
import diary.capstone.domain.file.FileService
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
import diary.capstone.util.*
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Service
@Transactional
class FeedService(
    private val feedRepository: FeedRepository,
    private val userService: UserService,
    private val fileService: FileService
) {

    // 피드의 사진을 저장
    private fun setAndSaveFiles(feed: Feed, form: FeedRequestForm) {
        if (form.images.size == form.descriptions.size) {
            // 이미지 파일들과 설명들을 합쳐서 하나의 리스트로 묶고 순회
            form.images.zip(form.descriptions) { file, desc ->
                fileService.saveFile(file, desc).setFeed(feed)
            }
        } else throw FeedException(INVALID_FEED_FORM)
    }

    // 피드 작성
    fun createFeed(form: FeedRequestForm, loginUser: User): Feed =
        feedRepository.save(
            Feed(
                writer = loginUser,
                content = form.content,
                showScope = form.showScope
            )
        ).let {
            setAndSaveFiles(it, form)
            feedRepository.flush()
            it
        }

    // 피드 목록 검색 (페이징)
    @Transactional(readOnly = true)
    fun getFeeds(pageable: Pageable, userId: Long?, loginUser: User): Page<Feed> =
        // 특정 유저의 피드만 조회 (프로필 통해 조회)
        userId?.let { userId ->
            val user = userService.getUser(userId)

            // 로그인 유저가 해당 유저일 경우 모든 피드 조회
            if (loginUser.id == userId)
                return getPagedFeed(pageable, user.feeds.sortedByDescending { it.id })

            return getPagedFeed(pageable,
                user.feeds
                    .filterNotShowFollowersFeed(
                        // 피드 작성자를 팔로우 했다면 팔로워 공개 피드를 보여줌
                        !user.follower.map { it.user.id }.contains(loginUser.id)
                    )
                    .filterNotShowMeFeed()
                    .sortedByDescending { it.id }
            )
        } ?:
        // 모든 피드 조회
        run {
            return feedRepository.findByShowScope(pageable, SHOW_ALL)
        }

    // 피드 목록 검색 (페이징, 피드라인으로)
    fun getFeedsByFeedLine(pageable: Pageable, feedLineId: Long, loginUser: User): Page<Feed> {
        loginUser.feedLines
            .find { it.id == feedLineId }
            ?.let {
                // TODO 피드 라인에 따른 피드 목록 조회 로직
            }
            .run { throw FeedException(FEED_LINE_NOT_FOUND) }
    }

    // Pageable, Feed 리스트로 페이징된 Feed 객체 반환
    @Transactional(readOnly = true)
    fun getPagedFeed(pageable: Pageable, feeds: List<Feed>): Page<Feed> {
        val total = feeds.size
        val start = pageable.offset.toInt()
        val end = min((start + pageable.pageSize), total)
        return PageImpl(feeds.subList(start, end), pageable, total.toLong())
    }

    // 피드 하나의 상세 정보
    @Transactional(readOnly = true)
    fun getFeed(feedId: Long): Feed =
        feedRepository.findById(feedId).orElseThrow { throw FeedException(FEED_NOT_FOUND) }

    // 피드 좋아요 등록
    fun likeFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let { feed ->
            if (feed.likes.none { it.user == loginUser })
                feed.likes.add(FeedLike(feed = feed, user = loginUser))
            else
                throw FeedLikeException(ALREADY_LIKED_FEED)
        }

    // 피드 좋아요 취소
    fun cancelLikeFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let { feed ->
            feed.likes.remove(
                feed.likes.find { it.user.id == loginUser.id }
            )
        }

    fun updateFeed(feedId: Long, form: FeedRequestForm, loginUser: User): Feed =
        getFeed(feedId).let { feed ->
            feedPermissionCheck(feed, loginUser)
            feed.update(form.content, form.showScope)
            setAndSaveFiles(feed, form)
            feed
        }

    fun deleteFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let {
            feedPermissionCheck(it, loginUser)
            feedRepository.delete(it)
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
    fun createRootComment(feedId: Long, form: CommentRequestForm, loginUser: User) =
        getFeed(feedId).let {
            it.comments.add(
                Comment(
                    feed = it,
                    writer = loginUser,
                    content = form.content
                )
            )
        }

    // 대댓글 생성
    fun createChildComment(feedId: Long, parentId: Long, form: CommentRequestForm, loginUser: User) =
        getComment(feedId, parentId).let {
            it.children.add(
                Comment(
                    feed = getFeed(feedId),
                    writer = loginUser,
                    content = form.content,
                    parent = it,
                    layer = it.layer + 1
                )
            )
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
                .filterNotSpecificUserComments(loginUser.id!!)
                .sortedBy { it.id }
        )

    // 해당 피드의 내가 쓴 루트 댓글만 조회 (부모 댓글 x, 로그인한 유저가 쓴 댓글만)
    @Transactional(readOnly = true)
    fun getMyComments(feedId: Long, pageable: Pageable, loginUser: User): Page<Comment> =
        getPagedComments(pageable,
            getFeed(feedId).comments
                .filterRootComments()
                .filterSpecificUserComments(loginUser.id!!)
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

    fun updateComment(feedId: Long, commentId: Long, form: CommentRequestForm, loginUser: User): Comment =
        getComment(feedId, commentId).let {
            commentPermissionCheck(it, loginUser)
            it.update(form.content)
        }

    fun deleteComment(feedId: Long, commentId: Long, loginUser: User) =
        getFeed(feedId).let {
            getComment(feedId, commentId).let { comment ->
                commentPermissionCheck(comment, loginUser)
                it.comments.remove(comment)
            }
        }

    // 댓글 접근 권한 체크
    private fun commentPermissionCheck(comment: Comment, loginUser: User) {
        if (comment.writer.id != loginUser.id) throw CommentException(COMMENT_ACCESS_DENIED)
    }
}