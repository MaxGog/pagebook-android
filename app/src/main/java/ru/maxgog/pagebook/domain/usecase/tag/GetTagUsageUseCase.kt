package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.TagUsage
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class GetTagUsageUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tagId: Int): TagUsage = repository.getTagUsage(tagId)
}