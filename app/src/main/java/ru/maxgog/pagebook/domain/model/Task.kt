package ru.maxgog.pagebook.domain.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val isAddedToCalendar: Boolean = false,
    val noteId: Int? = null,
    val tags: List<Tag> = emptyList()
)