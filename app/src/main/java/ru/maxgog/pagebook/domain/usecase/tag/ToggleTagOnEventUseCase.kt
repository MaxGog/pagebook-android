package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class ToggleTagOnEventUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(eventId: Int, tag: Tag): Boolean {
        val hasTag = repository.hasTagOnEvent(eventId, tag.id)
        return if (hasTag) {
            repository.removeTagFromEvent(eventId, tag.id)
            false
        } else {
            repository.addTagToEvent(eventId, tag.id)
            true
        }
    }
}