package diary.capstone.domain.report

import diary.capstone.util.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Report(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    var id: Long? = null,

    var reporterId: Long,
    var targetUserId: Long,
    var type: String,
    var reason: String,

): BaseTimeEntity() {

}