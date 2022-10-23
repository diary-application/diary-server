package diary.capstone.domain.setting

import diary.capstone.domain.user.User
import javax.persistence.*

@Entity
class Setting (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User,

    var lastLogin: String,
    var darkMode: Boolean = false,
    var profileShow: Boolean = true,
    
    // 유저가 저장한 피드들, id를 1,2,3 형태로 콤마(,)로 구분하여 저장
    var savedFeeds: String = "",
) {
    fun getSavedFeeds(): List<Long> {
        return if (this.savedFeeds == "") listOf()
        else this.savedFeeds.split(",").map { it.toLong() }
    }
}