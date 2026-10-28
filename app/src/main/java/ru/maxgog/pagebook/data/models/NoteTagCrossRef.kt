package ru.maxgog.pagebook.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["noteId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = NoteModel::class, parentColumns = ["NoteId"],
            childColumns = ["noteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TagModel::class, parentColumns = ["id"],
            childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("noteId"), Index("tagId")]
)
data class NoteTagCrossRef(val noteId: Int, val tagId: Int)