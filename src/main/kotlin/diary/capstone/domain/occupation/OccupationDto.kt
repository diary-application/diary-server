package diary.capstone.domain.occupation

data class OccupationResponse(var name: String) {
    constructor(occupation: Occupation): this(
        name = occupation.name
    )
}