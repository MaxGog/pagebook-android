package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.repositories.NotesRepository
import ru.maxgog.pagebook.data.rooms.NoteRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteRoomDatabase {
        return Room.databaseBuilder(
            context,
            NoteRoomDatabase::class.java,
            "notes_db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: NoteRoomDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(dao: NoteDao): NotesRepository {
        return NotesRepository(dao)
    }
}