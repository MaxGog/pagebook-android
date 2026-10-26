package ru.maxgog.pagebook.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TaskRepository
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) { operator fun invoke(): Flow<List<Task>> = repository.getAllTasks() }