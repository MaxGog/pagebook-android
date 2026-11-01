package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.local.AttachmentDao
import ru.maxgog.pagebook.data.mapper.toDomain
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.OwnerType
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val dao: AttachmentDao
) : AttachmentRepository {
    override fun getAttachmentsForOwner(type: OwnerType, id: Int): Flow<List<Attachment>> =
        dao.getForOwner(type.name, id).map { list -> list.map { it.toDomain() } }

    override fun getAllAttachments(): Flow<List<Attachment>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertAttachment(attachment: Attachment): Long =
        dao.insert(attachment.toEntity())

    override suspend fun updateAttachment(attachment: Attachment) =
        dao.update(attachment.toEntity())

    override suspend fun deleteAttachment(attachment: Attachment) =
        dao.delete(attachment.toEntity())
}