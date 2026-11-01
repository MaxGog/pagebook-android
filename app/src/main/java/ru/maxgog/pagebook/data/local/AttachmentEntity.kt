package ru.maxgog.pagebook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "attachments",
    indices = [Index("ownerType", "ownerId")]
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerType: String,
    val ownerId: Int,
    val type: String,
    val localPath: String,
    val mimeType: String,
    val displayName: String,
    val sizeBytes: Long,
    val createdAt: Long
)