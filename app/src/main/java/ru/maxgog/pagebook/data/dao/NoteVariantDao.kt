package ru.maxgog.pagebook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.NoteVariantModel

@Dao
interface NoteVariantDao {

    @Query("SELECT * FROM note_variants WHERE noteId = :noteId ORDER BY createdAt ASC")
    fun getForNote(noteId: Int): Flow<List<NoteVariantModel>>

    @Query("SELECT * FROM note_variants WHERE noteId = :noteId AND platform = :platform LIMIT 1")
    suspend fun getByPlatform(noteId: Int, platform: String): NoteVariantModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: NoteVariantModel): Long

    @Update
    suspend fun update(variant: NoteVariantModel)

    @Delete
    suspend fun delete(variant: NoteVariantModel)
}