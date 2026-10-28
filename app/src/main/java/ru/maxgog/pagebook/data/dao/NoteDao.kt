package ru.maxgog.pagebook.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.models.NoteWithTags

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY AtCreated DESC")
    fun getAll(): Flow<List<NoteModel>>

    @Insert
    suspend fun insert(note: NoteModel): Long

    @Update
    suspend fun update(note: NoteModel)

    @Delete
    suspend fun delete(note: NoteModel)

    @Query("SELECT * FROM notes WHERE NoteId = :id")
    suspend fun getById(id: Int): NoteModel?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY AtCreated DESC")
    fun getAllWithTags(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE NoteId = :id")
    suspend fun getWithTagsById(id: Int): NoteWithTags?
}