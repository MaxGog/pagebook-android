package ru.maxgog.pagebook.data.rooms

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.models.NoteModel

@Database(entities = [(NoteModel::class)], version = 1)
abstract class NoteRoomDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao

}