package ru.maxgog.pagebook.domain.usecase.task

import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int): Task? = repository.getTaskById(id)
}