package ru.maxgog.pagebook.domain.usecase.attachment

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.OwnerType
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import javax.inject.Inject

class GetAttachmentsForNoteUseCase @Inject constructor(
    private val repository: AttachmentRepository
) {
    operator fun invoke(noteId: Int): Flow<List<Attachment>> =
        repository.getAttachmentsForOwner(OwnerType.NOTE, noteId)
}