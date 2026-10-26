package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Note

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Int): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}