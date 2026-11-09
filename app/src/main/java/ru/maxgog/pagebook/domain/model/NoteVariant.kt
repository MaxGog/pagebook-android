package ru.maxgog.pagebook.domain.model

data class NoteVariant(
    val id: Int = 0,
    val noteId: Int,
    val platform: NotePlatform,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)