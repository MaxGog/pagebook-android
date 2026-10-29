package ru.maxgog.pagebook.domain.usecase.tag

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.repository.TagRepository
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(
        tag: Tag,
        newName: String,
        newColor: TagColor,
        newEmoji: String? = tag.emoji
    ) {
        require(tag.id > 0) { "Нельзя обновить тег без id" }
        val trimmed = newName.trim()
        require(trimmed.isNotEmpty()) { "Название тега не может быть пустым" }

        repository.updateTag(
            tag.copy(name = trimmed, colorHex = newColor.hex, emoji = newEmoji)
        )
    }
}