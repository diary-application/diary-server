package diary.capstone.domain.feed

import diary.capstone.domain.feed.comment.Comment
import diary.capstone.domain.feed.comment.CommentLike
import diary.capstone.domain.feed.comment.CommentRequestForm
import diary.capstone.domain.file.FileService
import diary.capstone.domain.user.SavedFeed
import diary.capstone.domain.user.User
import diary.capstone.domain.user.UserService
import diary.capstone.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
            feed.files.forEach { fileService.deleteFile(it) }
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
    fun getFeeds(pageable: Pageable, userId: Long?, feedLineId: Long?, loginUser: User): Page<Feed> {
        // 특정 유저의 피드만 조회 (프로필 통해 조회)
        userId?.let { userId ->
            // 로그인 유저가 해당 유저일 경우 모든 피드 조회
            if (loginUser.id == userId)
                return getPagedFeed(pageable,
                    loginUser.feeds.sortedByDescending { it.id }
                )

            val user = userService.getUser(userId)

            return getPagedFeed(pageable,
                user.feeds
                    .filterNotShowFollowersFeed(
                        // 피드 작성자를 팔로우 했다면 팔로워 공개 피드를 보여줌
                        !user.follower.map { it.user.id }.contains(loginUser.id)
                    )
                    .filterNotShowMeFeed()
                    .sortedByDescending { it.id }
            )
        }
        // 피드라인으로 조회
        feedLineId?.let {
            val feedLine = loginUser.feedLines.find { feedLine ->  feedLine.id == it }
            // TODO 피드라인으로 피드 목록 조회 로직
        }

        // 모든 피드 조회
        return feedRepository.findByShowScope(pageable, SHOW_ALL)
    }

    // 피드 내용 또는 파일 설명으로 피드 조회
    @Transactional(readOnly = true)
    fun searchFeedsByUserAndKeyword(pageable: Pageable, userId: Long, keyword: String): Page<Feed> =
        getPagedFeed(pageable,
            userService.getUser(userId).feeds
                .filter { feed ->
                    feed.content.contains(keyword) ||
                            feed.files.any { it.description.contains(keyword) }
                }
                .sortedByDescending { it.id }
        )

    // 피드 하나의 상세 정보
    @Transactional(readOnly = true)
    fun getFeed(feedId: Long): Feed =
        feedRepository.findById(feedId).orElseThrow { throw FeedException(FEED_NOT_FOUND) }

    @Transactional(readOnly = true)
    fun getFeedLikes(pageable: Pageable, feedId: Long): List<User> =
        getFeed(feedId).let { feed -> feed.likes.map { it.user } }

    // 피드 좋아요 등록
    fun likeFeed(feedId: Long, loginUser: User): Feed =
        getFeed(feedId).let { feed ->
            if (feed.likes.none { it.user.id == loginUser.id })
                feed.likes.add(FeedLike(feed = feed, user = loginUser))
            else throw FeedLikeException(ALREADY_LIKED_FEED)
            feed
        }

    // 피드 좋아요 취소
    fun cancelLikeFeed(feedId: Long, loginUser: User): Feed =
        getFeed(feedId).let { feed ->
            feed.likes.remove(
                feed.likes.find { it.user.id == loginUser.id }
            )
            feed
        }

    // 피드 저장
    fun saveFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let { feed ->
            if (feed.saves.none { it.user == loginUser })
                feed.saves.add(SavedFeed(user = loginUser, feed = feed))
            else throw FeedSaveException(ALREADY_SAVED_FEED)
        }

    // 피드 저장 삭제
    fun removeSavedFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let { feed ->
            feed.saves.remove(feed.saves.find { it.user.id == loginUser.id })
        }

    fun updateFeed(feedId: Long, form: FeedRequestForm, loginUser: User): Feed =
        getFeed(feedId).let { feed ->
            feedPermissionCheck(feed, loginUser)
            feed.update(form.content, form.showScope)
            setAndSaveFiles(feed, form)
            feed
        }

    fun deleteFeed(feedId: Long, loginUser: User) =
        getFeed(feedId).let { feed ->
            feedPermissionCheck(feed, loginUser)
            feed.files.forEach { fileService.deleteFile(it) }
            feedRepository.delete(feed)
        }

    // 피드 접근 권한 체크
    private fun feedPermissionCheck(feed: Feed, loginUser: User) {
        if (feed.writer.id != loginUser.id) throw FeedException(FEED_ACCESS_DENIED)
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
                .sortedByDescending { it.id }
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

    // 댓글 수정
    fun updateComment(feedId: Long, commentId: Long, form: CommentRequestForm, loginUser: User): Comment =
        getComment(feedId, commentId).let {
            commentPermissionCheck(it, loginUser)
            it.update(form.content)
        }

    // 댓글 삭제
    fun deleteComment(feedId: Long, commentId: Long, loginUser: User) =
        getFeed(feedId).let {
            getComment(feedId, commentId).let { comment ->
                commentPermissionCheck(comment, loginUser)
                it.comments.remove(comment)
            }
        }

    // 댓글 좋아요한 사람 목록 조회
    @Transactional(readOnly = true)
    fun getCommentLikes(feedId: Long, commentId: Long, loginUser: User): List<User> =
        getComment(feedId, commentId).let { comment ->
            comment.likes.map { it.user }
        }

    // 피드 좋아요 등록
    fun likeComment(feedId: Long, commentId: Long, loginUser: User): Comment =
        getComment(feedId, commentId).let { comment ->
            if (comment.likes.none { it.user.id == loginUser.id })
                comment.likes.add(CommentLike(user = loginUser, comment = comment))
            else throw CommentLikeException(ALREADY_LIKED_COMMENT)
            comment
        }

    // 피드 좋아요 취소
    fun cancelLikeComment(feedId: Long, commentId: Long, loginUser: User): Comment =
        getComment(feedId, commentId).let { comment ->
            comment.likes.remove(
                comment.likes.find { it.user.id == loginUser.id }
            )
            comment
        }

    // 댓글 접근 권한 체크
    private fun commentPermissionCheck(comment: Comment, loginUser: User) {
        if (comment.writer.id != loginUser.id) throw CommentException(COMMENT_ACCESS_DENIED)
    }
}