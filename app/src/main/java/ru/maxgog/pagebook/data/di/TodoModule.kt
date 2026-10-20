package ru.maxgog.pagebook.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.dao.TodoDao
import ru.maxgog.pagebook.data.repositories.TodoRepository
import ru.maxgog.pagebook.data.rooms.TodoRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoRoomDatabase {
        return Room.databaseBuilder(
            context,
            TodoRoomDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    fun provideTodoDao(database: TodoRoomDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(dao: TodoDao): TodoRepository {
        return TodoRepository(dao)
    }
}
