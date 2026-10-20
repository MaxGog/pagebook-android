package ru.maxgog.pagebook.data.rooms

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.maxgog.pagebook.data.dao.TodoDao
import ru.maxgog.pagebook.data.models.TodoModel

@Database(entities = [TodoModel::class], version = 1)
abstract class TodoRoomDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}