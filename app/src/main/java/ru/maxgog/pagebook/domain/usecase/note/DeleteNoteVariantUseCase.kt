package ru.maxgog.pagebook.domain.usecase.note

import ru.maxgog.pagebook.domain.model.NoteVariant
import ru.maxgog.pagebook.domain.repository.NoteVariantRepository
import javax.inject.Inject

class DeleteNoteVariantUseCase @Inject constructor(
    private val repository: NoteVariantRepository
) {
    suspend operator fun invoke(variant: NoteVariant) =
        repository.deleteVariant(variant)
}