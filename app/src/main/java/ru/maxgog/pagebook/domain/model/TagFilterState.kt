package ru.maxgog.pagebook.domain.model

enum class FilterMode { OR, AND }

enum class TagFilterModule { NOTES, TASKS, CALENDAR }

data class TagFilterState(
    val tagIds: Set<Int> = emptySet(),
    val mode: FilterMode = FilterMode.OR
) {
    val isEmpty: Boolean get() = tagIds.isEmpty()
    val count: Int get() = tagIds.size
}