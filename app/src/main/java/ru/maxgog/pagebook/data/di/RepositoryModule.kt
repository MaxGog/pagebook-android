package ru.maxgog.pagebook.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.repository.EventRepositoryImpl
import ru.maxgog.pagebook.data.repository.NoteRepositoryImpl
import ru.maxgog.pagebook.data.repository.TaskRepositoryImpl
import ru.maxgog.pagebook.data.repository.TagRepositoryImpl
import ru.maxgog.pagebook.domain.repository.EventRepository
import ru.maxgog.pagebook.domain.repository.NoteRepository
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.repository.TaskRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository
}