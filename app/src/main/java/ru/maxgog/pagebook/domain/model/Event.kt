package ru.maxgog.pagebook.domain.model

import kotlinx.datetime.LocalDate

data class Event(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate?,
    val time: String = "12:00",
    val tags: List<Tag> = emptyList()
)