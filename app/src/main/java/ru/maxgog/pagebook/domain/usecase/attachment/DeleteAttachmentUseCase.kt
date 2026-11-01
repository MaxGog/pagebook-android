package ru.maxgog.pagebook.domain.usecase.attachment

import ru.maxgog.pagebook.data.file.AttachmentFileStore
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import javax.inject.Inject

class DeleteAttachmentUseCase @Inject constructor(
    private val repository: AttachmentRepository,
    private val fileStore: AttachmentFileStore
) {
    suspend operator fun invoke(attachment: Attachment) {
        repository.deleteAttachment(attachment)
        fileStore.delete(attachment.localPath)
    }
}