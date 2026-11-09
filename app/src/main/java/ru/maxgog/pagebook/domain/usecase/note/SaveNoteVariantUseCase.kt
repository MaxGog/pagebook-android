package ru.maxgog.pagebook.domain.usecase.note

import ru.maxgog.pagebook.domain.model.NoteVariant
import ru.maxgog.pagebook.domain.repository.NoteVariantRepository
import javax.inject.Inject

class SaveNoteVariantUseCase @Inject constructor(
    private val repository: NoteVariantRepository
) {
    suspend operator fun invoke(variant: NoteVariant): Long =
        repository.saveVariant(variant)
}