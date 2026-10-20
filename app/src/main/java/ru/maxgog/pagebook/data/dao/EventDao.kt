package ru.maxgog.pagebook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import ru.maxgog.pagebook.data.models.EventModel
import java.util.Date

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: EventModel)

    @Delete
    suspend fun delete(event: EventModel)

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    suspend fun getAllEvents(): List<EventModel>
}