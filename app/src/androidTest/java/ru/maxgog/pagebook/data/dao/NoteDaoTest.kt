package ru.maxgog.pagebook.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.rooms.NoteRoomDatabase

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var database: NoteRoomDatabase
    private lateinit var noteDao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NoteRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetNoteById() = runTest {
        val note = NoteModel(title = "Тестовая заметка", content = "Контент")
        noteDao.insert(note)

        val loadedNote = noteDao.getById(note.id).first()

        assertThat(loadedNote).isNotNull()
        assertThat(loadedNote?.title).isEqualTo("Тестовая заметка")
    }

    @Test
    fun getAllNotes_returnsEmptyListInitially() = runTest {
        val notes = noteDao.getAll().first()

        assertThat(notes).isEmpty()
    }
}