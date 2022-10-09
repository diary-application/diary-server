package diary.capstone.domain.file

import diary.capstone.config.FILE_SAVE_PATH
import diary.capstone.domain.feed.Feed
import javax.persistence.*

@Entity
class File (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    var id: Long? = null,

    var originalName: String = "",
    var savedName: String = "",
    
    // 피드의 경우 해당 사진에 대한 설명이 함께 첨부될 수 있음.
    var description: String = "",

    // 파일 타입 구분(피드 첨부 이미지, 프로필 이미지..)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    var feedFile: Feed? = null
) {
    // 파일이 등록된 피드 설정
    fun setFeed(feed: Feed): File {
        this.feedFile = feed
        return this
    }

    // 엔티티 삭제 전 서버에 저장된 해당 파일 삭제
    @PreRemove
    fun deleteStoredFile() { java.io.File(FILE_SAVE_PATH + this.savedName).delete() }
}