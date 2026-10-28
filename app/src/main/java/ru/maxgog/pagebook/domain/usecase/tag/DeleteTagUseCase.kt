package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tag: Tag) = repository.deleteTag(tag)
}