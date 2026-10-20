package ru.maxgog.pagebook.data.repositories

import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.models.EventModel

class EventRepository(private val eventDao: EventDao) {
    suspend fun insert(event: EventModel) = eventDao.insert(event)
    suspend fun delete(event: EventModel) = eventDao.delete(event)
    suspend fun getAllEvents(): List<EventModel> = eventDao.getAllEvents()
}