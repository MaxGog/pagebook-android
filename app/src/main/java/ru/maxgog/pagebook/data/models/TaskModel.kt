package ru.maxgog.pagebook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [Index("NoteId")]
)
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Title") val title: String,
    @ColumnInfo(name = "Description") val description: String? = null,
    @ColumnInfo(name = "IsCompleted") val isCompleted: Boolean = false,
    @ColumnInfo(name = "AtCreated") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "HasReminder") val hasReminder: Boolean = false,
    @ColumnInfo(name = "ReminderTime") val reminderTime: Long? = null,
    @ColumnInfo(name = "IsAddedToCalendar") val isAddedToCalendar: Boolean = false,
    @ColumnInfo(name = "NoteId") val noteId: Int? = null
)