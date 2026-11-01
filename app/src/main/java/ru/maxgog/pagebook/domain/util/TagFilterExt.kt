package ru.maxgog.pagebook.domain.util

import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagFilterState

fun <T> List<T>.applyTagFilter(
    filter: TagFilterState,
    tagsOf: (T) -> List<Tag>
): List<T> {
    if (filter.tagIds.isEmpty()) return this
    return filter { item ->
        val itemTagIds = tagsOf(item).mapTo(mutableSetOf()) { it.id }
        when (filter.mode) {
            FilterMode.OR  -> itemTagIds.any { it in filter.tagIds }
            FilterMode.AND -> filter.tagIds.all { it in itemTagIds }
        }
    }
}