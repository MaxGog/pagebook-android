package ru.maxgog.pagebook.data.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "NoteId") val id: Int = 0,
    @ColumnInfo(name = "Title") val title: String = "",
    @ColumnInfo(name = "Content") val content: String = "",
    //@ColumnInfo(name = "Color") val color: Int = 0xFFBB86FC.toInt(),
    @ColumnInfo(name = "AtCreated") val createdAt: Long = System.currentTimeMillis()
)