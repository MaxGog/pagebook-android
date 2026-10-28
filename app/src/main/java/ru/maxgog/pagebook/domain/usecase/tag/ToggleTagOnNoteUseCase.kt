package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class ToggleTagOnNoteUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(noteId: Int, tag: Tag): Boolean {
        val hasTag = repository.hasTagOnNote(noteId, tag.id)
        return if (hasTag) {
            repository.removeTagFromNote(noteId, tag.id)
            false
        } else {
            repository.addTagToNote(noteId, tag.id)
            true
        }
    }
}