package ru.maxgog.pagebook.domain.usecase.task

import jakarta.inject.Inject
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TaskRepository

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) { suspend operator fun invoke(task: Task) = repository.deleteTask(task) }