package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.repository.NoteRepositoryImpl
import ru.maxgog.pagebook.data.rooms.NoteRoomDatabase
import ru.maxgog.pagebook.domain.repository.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteRoomDatabase =
        Room.databaseBuilder(context, NoteRoomDatabase::class.java, "notes_db").build()

    @Provides
    fun provideNoteDao(database: NoteRoomDatabase): NoteDao = database.noteDao()

    @Provides
    @Singleton
    fun provideNotesRepository(dao: NoteDao): NoteRepository = NoteRepositoryImpl(dao)
}