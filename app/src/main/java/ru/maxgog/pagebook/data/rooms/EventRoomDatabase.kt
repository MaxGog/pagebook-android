package ru.maxgog.pagebook.data.rooms

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maxgog.pagebook.Converters
import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.models.EventModel

@Database(entities = [EventModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}