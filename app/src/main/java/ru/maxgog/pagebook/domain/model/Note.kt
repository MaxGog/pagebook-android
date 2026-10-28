package ru.maxgog.pagebook.domain.model

data class Note(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",       // HTML-строка
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<Tag> = emptyList()
)