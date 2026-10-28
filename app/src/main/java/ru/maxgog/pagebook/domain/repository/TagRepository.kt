package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Tag

interface TagRepository {
    // CRUD для тегов
    fun getAllTags(): Flow<List<Tag>>
    suspend fun createTag(tag: Tag): Long
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)

    // Теги для конкретной сущности
    fun getTagsForNote(noteId: Int): Flow<List<Tag>>
    fun getTagsForTask(taskId: Int): Flow<List<Tag>>
    fun getTagsForEvent(eventId: Int): Flow<List<Tag>>

    // Проверка наличия тега (для Toggle)
    suspend fun hasTagOnNote(noteId: Int, tagId: Int): Boolean
    suspend fun hasTagOnTask(taskId: Int, tagId: Int): Boolean
    suspend fun hasTagOnEvent(eventId: Int, tagId: Int): Boolean

    // Привязка / отвязка
    suspend fun addTagToNote(noteId: Int, tagId: Int)
    suspend fun removeTagFromNote(noteId: Int, tagId: Int)
    suspend fun addTagToTask(taskId: Int, tagId: Int)
    suspend fun removeTagFromTask(taskId: Int, tagId: Int)
    suspend fun addTagToEvent(eventId: Int, tagId: Int)
    suspend fun removeTagFromEvent(eventId: Int, tagId: Int)
}