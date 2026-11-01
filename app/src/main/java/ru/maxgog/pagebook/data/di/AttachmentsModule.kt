package ru.maxgog.pagebook.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.maxgog.pagebook.data.repository.AttachmentRepositoryImpl
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AttachmentsModule {
    @Binds
    @Singleton
    abstract fun bindAttachmentRepository(impl: AttachmentRepositoryImpl): AttachmentRepository
}