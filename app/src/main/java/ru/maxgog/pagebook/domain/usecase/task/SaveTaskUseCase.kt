package ru.maxgog.pagebook.domain.usecase.task

import jakarta.inject.Inject
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TaskRepository

class SaveTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long {
        return if (task.id == 0) {
            repository.insertTask(task)
        } else {
            repository.updateTask(task)
            task.id.toLong()
        }
    }
}