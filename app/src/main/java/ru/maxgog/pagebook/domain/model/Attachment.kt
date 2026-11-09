package ru.maxgog.pagebook.domain.model

enum class AttachmentType { IMAGE, VIDEO, AUDIO, FILE }

enum class OwnerType { NOTE, TASK, EVENT, NOTE_VARIANT }

data class Attachment(
    val id: Long = 0,
    val ownerType: OwnerType,
    val ownerId: Int,
    val type: AttachmentType,
    val localPath: String,
    val mimeType: String,
    val displayName: String,
    val sizeBytes: Long,
    val createdAt: Long = System.currentTimeMillis()
)