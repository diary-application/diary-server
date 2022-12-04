package diary.capstone.domain.file

import diary.capstone.domain.feed.Feed
import diary.capstone.domain.user.User
import javax.persistence.*

@Entity
class File (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    var id: Long? = null,

    var originalName: String = "",
    var source: String = "",
    
    // 피드의 경우 해당 사진에 대한 설명이 함께 첨부될 수 있음.
    var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    var feedFile: Feed? = null,

    var sequence: Int = 1
) {

    // 파일이 등록된 피드 설정
    fun setFeed(feed: Feed?): File {
        this.feedFile = feed
        return this
    }

    // 파일 설명 수정
    fun updateDesc(description: String): File {
        this.description = description
        return this
    }

    fun setSequence(seq: Int): File {
        this.sequence = seq
        return this
    }
}