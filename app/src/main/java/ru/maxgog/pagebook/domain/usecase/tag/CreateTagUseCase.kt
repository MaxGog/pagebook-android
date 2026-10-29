package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class CreateTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(
        name: String,
        color: TagColor,
        emoji: String? = null
    ): Long {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Название тега не может быть пустым" }

        return repository.createTag(
            Tag(
                name = trimmed,
                colorHex = color.hex,
                emoji = emoji
            )
        )
    }
}