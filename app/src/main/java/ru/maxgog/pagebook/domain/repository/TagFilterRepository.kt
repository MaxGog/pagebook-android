package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.TagFilterModule
import ru.maxgog.pagebook.domain.model.TagFilterState

interface TagFilterRepository {
    fun getFilter(module: TagFilterModule): Flow<TagFilterState>
    suspend fun setFilter(module: TagFilterModule, state: TagFilterState)
    suspend fun clearFilter(module: TagFilterModule)
}