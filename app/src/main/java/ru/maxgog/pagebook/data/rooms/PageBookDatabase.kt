package ru.maxgog.pagebook.data.rooms

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maxgog.pagebook.Converters
import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.dao.TagDao
import ru.maxgog.pagebook.data.dao.TaskDao
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.models.TaskModel
import ru.maxgog.pagebook.data.models.EventModel
import ru.maxgog.pagebook.data.models.TagModel
import ru.maxgog.pagebook.data.models.NoteTagCrossRef
import ru.maxgog.pagebook.data.models.TaskTagCrossRef
import ru.maxgog.pagebook.data.models.EventTagCrossRef

@Database(
    entities = [
        NoteModel::class,
        TaskModel::class,
        EventModel::class,
        TagModel::class,
        NoteTagCrossRef::class,
        TaskTagCrossRef::class,
        EventTagCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PageBookDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun tagDao(): TagDao
}