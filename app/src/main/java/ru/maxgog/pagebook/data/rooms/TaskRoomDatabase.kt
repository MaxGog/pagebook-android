package ru.maxgog.pagebook.data.rooms

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.maxgog.pagebook.data.dao.TaskDao
import ru.maxgog.pagebook.data.models.TaskModel

@Database(entities = [TaskModel::class], version = 1)
abstract class TaskRoomDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}