package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.mapper.toDomain
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.repository.NoteRepository
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllWithTags().map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Int): Note? =
        noteDao.getWithTagsById(id)?.toDomain()

    override suspend fun insertNote(note: Note): Long =
        noteDao.insert(note.toEntity())

    override suspend fun updateNote(note: Note) =
        noteDao.update(note.toEntity())

    override suspend fun deleteNote(note: Note) =
        noteDao.delete(note.toEntity())
}