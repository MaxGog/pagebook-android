package ru.maxgog.pagebook.data.repositories

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.dao.NoteDao
import ru.maxgog.pagebook.data.models.NoteModel

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteModel>> = noteDao.getAll()

    suspend fun insert(note: NoteModel) = noteDao.insert(note)
    suspend fun update(note: NoteModel) = noteDao.update(note)
    suspend fun delete(note: NoteModel) = noteDao.delete(note)
    
    fun getNoteById(id: Int): Flow<NoteModel?> = noteDao.getById(id)
}