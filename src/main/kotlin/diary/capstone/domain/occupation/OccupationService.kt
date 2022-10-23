package diary.capstone.domain.occupation

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OccupationService(private val occupationRepository: OccupationRepository) {

    fun createOccupation(name: String): Occupation {
        if (occupationRepository.existsByName(name)) throw OccupationException(DUPLICATE_OCCUPATION)
        return occupationRepository.save(Occupation(name = name))
    }

    @Transactional(readOnly = true)
    fun getOccupation(name: String): Occupation =
        occupationRepository.findByName(name) ?: throw OccupationException(OCCUPATION_NOT_FOUND)

    @Transactional(readOnly = true)
    fun getOccupations(): List<Occupation> = occupationRepository.findAll()

    @Transactional(readOnly = true)
    fun isExists(name: String): Boolean = occupationRepository.existsByName(name)

    fun updateOccupation(occupationId: Long, form: OccupationRequestForm) =
        getOccupations()
            .find { it.id == occupationId }?.update(form.name)
            ?: run { throw OccupationException(OCCUPATION_NOT_FOUND) }

    fun deleteOccupation(name: String) = occupationRepository.deleteByName(name)
}