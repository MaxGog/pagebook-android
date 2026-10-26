package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.TaskDao
import ru.maxgog.pagebook.data.repository.TaskRepositoryImpl
import ru.maxgog.pagebook.data.rooms.TaskRoomDatabase
import ru.maxgog.pagebook.domain.repository.TaskRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskRoomDatabase =
        Room.databaseBuilder(context, TaskRoomDatabase::class.java, "tasks_db")
            .fallbackToDestructiveMigration() // временно, пока нет миграций
            .build()

    @Provides
    fun provideTaskDao(database: TaskRoomDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository = TaskRepositoryImpl(dao)
}