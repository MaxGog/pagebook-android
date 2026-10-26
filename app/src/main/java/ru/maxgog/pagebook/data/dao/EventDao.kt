package ru.maxgog.pagebook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.EventModel

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<EventModel>>

    @Insert
    suspend fun insert(event: EventModel)

    @Update
    suspend fun update(event: EventModel)

    @Delete
    suspend fun delete(event: EventModel)
}