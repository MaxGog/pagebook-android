package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Event

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
}