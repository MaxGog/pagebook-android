package ru.maxgog.pagebook.domain.model

data class TagUsage(
    val notes: Int = 0,
    val tasks: Int = 0,
    val events: Int = 0
) {
    val total: Int get() = notes + tasks + events
}