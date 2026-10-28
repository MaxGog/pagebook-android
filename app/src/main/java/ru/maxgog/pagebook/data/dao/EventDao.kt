package ru.maxgog.pagebook.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.EventModel
import ru.maxgog.pagebook.data.models.EventWithTags

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<EventModel>>

    @Insert
    suspend fun insert(event: EventModel): Long

    @Update
    suspend fun update(event: EventModel)

    @Delete
    suspend fun delete(event: EventModel)

    @Transaction
    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllWithTags(): Flow<List<EventWithTags>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getWithTagsById(id: Int): EventWithTags?
}