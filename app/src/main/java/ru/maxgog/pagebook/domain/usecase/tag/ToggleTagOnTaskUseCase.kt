package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class ToggleTagOnTaskUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(taskId: Int, tag: Tag): Boolean {
        val hasTag = repository.hasTagOnTask(taskId, tag.id)
        return if (hasTag) {
            repository.removeTagFromTask(taskId, tag.id)
            false
        } else {
            repository.addTagToTask(taskId, tag.id)
            true
        }
    }
}