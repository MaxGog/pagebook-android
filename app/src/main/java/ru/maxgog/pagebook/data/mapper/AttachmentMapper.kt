package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.local.AttachmentEntity
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.AttachmentType
import ru.maxgog.pagebook.domain.model.OwnerType

fun AttachmentEntity.toDomain(): Attachment = Attachment(
    id = id,
    ownerType = OwnerType.valueOf(ownerType),
    ownerId = ownerId,
    type = AttachmentType.valueOf(type),
    localPath = localPath,
    mimeType = mimeType,
    displayName = displayName,
    sizeBytes = sizeBytes,
    createdAt = createdAt
)

fun Attachment.toEntity(): AttachmentEntity = AttachmentEntity(
    id = id,
    ownerType = ownerType.name,
    ownerId = ownerId,
    type = type.name,
    localPath = localPath,
    mimeType = mimeType,
    displayName = displayName,
    sizeBytes = sizeBytes,
    createdAt = createdAt
)