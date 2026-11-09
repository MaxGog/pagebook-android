package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.NoteVariantModel
import ru.maxgog.pagebook.domain.model.NotePlatform
import ru.maxgog.pagebook.domain.model.NoteVariant

fun NoteVariantModel.toDomain(): NoteVariant = NoteVariant(
    id = id,
    noteId = noteId,
    platform = NotePlatform.valueOf(platform),
    title = title,
    content = content,
    createdAt = createdAt
)

fun NoteVariant.toEntity(): NoteVariantModel = NoteVariantModel(
    id = id,
    noteId = noteId,
    platform = platform.name,
    title = title,
    content = content,
    createdAt = createdAt
)