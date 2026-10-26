package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.mapper.toDomain
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> =
        eventDao.getAllEvents().map { models -> models.map { it.toDomain() } }

    override suspend fun insertEvent(event: Event) =
        eventDao.insert(event.toEntity())

    override suspend fun updateEvent(event: Event) =
        eventDao.update(event.toEntity())

    override suspend fun deleteEvent(event: Event) =
        eventDao.delete(event.toEntity())
}