package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.domain.model.Note

fun NoteModel.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)

fun Note.toEntity(): NoteModel = NoteModel(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)