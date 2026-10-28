package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.dao.TaskDao
import ru.maxgog.pagebook.data.mapper.toDomain
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllWithTags().map { list -> list.map { it.toDomain() } }

    override fun getTasksByNoteId(noteId: Int): Flow<List<Task>> =
        taskDao.getTasksByNoteId(noteId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertTask(task: Task): Long =
        taskDao.insert(task.toEntity())

    override suspend fun updateTask(task: Task) =
        taskDao.update(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        taskDao.delete(task.toEntity())

    override suspend fun getTaskById(id: Int): Task? =
        taskDao.getWithTagsById(id)?.toDomain()
}