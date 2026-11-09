package ru.maxgog.pagebook.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_variants",
    foreignKeys = [
        ForeignKey(
            entity = NoteModel::class,
            parentColumns = ["NoteId"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("noteId"),
        Index(value = ["noteId", "platform"], unique = true)
    ]
)
data class NoteVariantModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noteId: Int,
    val platform: String,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)