package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.TagModel
import ru.maxgog.pagebook.domain.model.Tag

fun TagModel.toDomain(): Tag = Tag(
    id = id,
    name = name,
    colorHex = colorHex,
    emoji = emoji
)

fun Tag.toEntity(): TagModel = TagModel(
    id = id,
    name = name,
    colorHex = colorHex,
    emoji = emoji
)

fun List<TagModel>.toDomainList(): List<Tag> = map { it.toDomain() }