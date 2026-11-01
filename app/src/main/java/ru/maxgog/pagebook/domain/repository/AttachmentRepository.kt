package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.OwnerType

interface AttachmentRepository {
    fun getAttachmentsForOwner(type: OwnerType, id: Int): Flow<List<Attachment>>
    fun getAllAttachments(): Flow<List<Attachment>>
    suspend fun insertAttachment(attachment: Attachment): Long
    suspend fun updateAttachment(attachment: Attachment)
    suspend fun deleteAttachment(attachment: Attachment)
}