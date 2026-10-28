package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.dao.TagDao
import ru.maxgog.pagebook.data.mapper.toDomainList
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.data.models.EventTagCrossRef
import ru.maxgog.pagebook.data.models.NoteTagCrossRef
import ru.maxgog.pagebook.data.models.TaskTagCrossRef
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    // CRUD
    override fun getAllTags(): Flow<List<Tag>> =
        tagDao.getAllTags().map { it.toDomainList() }

    override suspend fun createTag(tag: Tag): Long =
        tagDao.insertTag(tag.toEntity())

    override suspend fun updateTag(tag: Tag) =
        tagDao.updateTag(tag.toEntity())

    override suspend fun deleteTag(tag: Tag) =
        tagDao.deleteTag(tag.toEntity())

    // Чтение тегов сущности
    override fun getTagsForNote(noteId: Int): Flow<List<Tag>> =
        tagDao.getTagsForNote(noteId).map { it.toDomainList() }

    override fun getTagsForTask(taskId: Int): Flow<List<Tag>> =
        tagDao.getTagsForTask(taskId).map { it.toDomainList() }

    override fun getTagsForEvent(eventId: Int): Flow<List<Tag>> =
        tagDao.getTagsForEvent(eventId).map { it.toDomainList() }

    // Проверка наличия
    override suspend fun hasTagOnNote(noteId: Int, tagId: Int): Boolean =
        tagDao.hasTagOnNote(noteId, tagId)

    override suspend fun hasTagOnTask(taskId: Int, tagId: Int): Boolean =
        tagDao.hasTagOnTask(taskId, tagId)

    override suspend fun hasTagOnEvent(eventId: Int, tagId: Int): Boolean =
        tagDao.hasTagOnEvent(eventId, tagId)

    // Привязка / отвязка Note
    override suspend fun addTagToNote(noteId: Int, tagId: Int) {
        tagDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId = noteId, tagId = tagId))
    }

    override suspend fun removeTagFromNote(noteId: Int, tagId: Int) {
        tagDao.deleteNoteTagCrossRef(noteId, tagId)
    }

    // Task
    override suspend fun addTagToTask(taskId: Int, tagId: Int) {
        tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId = taskId, tagId = tagId))
    }

    override suspend fun removeTagFromTask(taskId: Int, tagId: Int) {
        tagDao.deleteTaskTagCrossRef(taskId, tagId)
    }

    // Event
    override suspend fun addTagToEvent(eventId: Int, tagId: Int) {
        tagDao.insertEventTagCrossRef(EventTagCrossRef(eventId = eventId, tagId = tagId))
    }

    override suspend fun removeTagFromEvent(eventId: Int, tagId: Int) {
        tagDao.deleteEventTagCrossRef(eventId, tagId)
    }
}