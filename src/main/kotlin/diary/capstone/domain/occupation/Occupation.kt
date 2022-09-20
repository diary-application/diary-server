package diary.capstone.domain.occupation;

import javax.persistence.*;

@Entity
class Occupation (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupation_id")
    var id: Long? = null,

    var name: String,
)