package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.dao.NoteVariantDao
import ru.maxgog.pagebook.data.dao.TagDao
import ru.maxgog.pagebook.data.dao.TaskDao
import ru.maxgog.pagebook.data.local.AttachmentDao
import ru.maxgog.pagebook.data.rooms.PageBookDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PageBookDatabase = Room.databaseBuilder(
        context,
        PageBookDatabase::class.java,
        "pagebook.db"
    )
        .build()

    @Provides
    fun provideNoteDao(db: PageBookDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideTaskDao(db: PageBookDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideEventDao(db: PageBookDatabase): EventDao = db.eventDao()

    @Provides
    fun provideTagDao(db: PageBookDatabase): TagDao = db.tagDao()

    @Provides
    fun provideAttachmentDao(db: PageBookDatabase): AttachmentDao = db.attachmentDao()

    @Provides
    fun provideNoteVariantDao(db: PageBookDatabase): NoteVariantDao = db.noteVariantDao()
}