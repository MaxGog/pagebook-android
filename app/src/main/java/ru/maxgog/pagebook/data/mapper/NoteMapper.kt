package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.models.NoteWithTags
import ru.maxgog.pagebook.domain.model.Note

fun NoteModel.toDomain(tags: List<ru.maxgog.pagebook.domain.model.Tag> = emptyList()): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    tags = tags
)

fun NoteWithTags.toDomain(): Note =
    note.toDomain(tags = tags.map { it.toDomain() })

fun Note.toEntity(): NoteModel = NoteModel(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)