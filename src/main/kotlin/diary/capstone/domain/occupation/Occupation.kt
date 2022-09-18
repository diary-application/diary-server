package diary.capstone.domain.occupation;

import javax.persistence.*;

@Entity
class Occupation (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var name: String,
)