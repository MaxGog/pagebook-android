package ru.maxgog.pagebook.domain.usecase.tag

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class GetTagsForNoteUseCase @Inject constructor(
    private val repository: TagRepository
) {
    operator fun invoke(noteId: Int): Flow<List<Tag>> =
        repository.getTagsForNote(noteId)
}