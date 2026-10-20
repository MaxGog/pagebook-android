package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.EventDao
import ru.maxgog.pagebook.data.repositories.EventRepository
import ru.maxgog.pagebook.data.rooms.EventRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

    @Provides
    @Singleton
    fun provideEventDatabase(@ApplicationContext context: Context): EventRoomDatabase {
        return Room.databaseBuilder(
            context,
            EventRoomDatabase::class.java,
            "events_db"
        ).build()
    }

    @Provides
    fun provideEventDao(database: EventRoomDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideEventRepository(dao: EventDao): EventRepository {
        return EventRepository(dao)
    }
}