package ru.maxgog.pagebook.domain.usecase.note

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.NoteVariant
import ru.maxgog.pagebook.domain.repository.NoteVariantRepository
import javax.inject.Inject

class GetVariantsForNoteUseCase @Inject constructor(
    private val repository: NoteVariantRepository
) {
    operator fun invoke(noteId: Int): Flow<List<NoteVariant>> =
        repository.getVariantsForNote(noteId)
}