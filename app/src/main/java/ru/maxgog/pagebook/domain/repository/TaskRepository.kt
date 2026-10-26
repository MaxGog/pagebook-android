package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByNoteId(noteId: Int): Flow<List<Task>>
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)

    suspend fun getTaskById(id: Int): Task?
}