package ru.maxgog.pagebook.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE ownerType = :type AND ownerId = :id ORDER BY createdAt ASC")
    fun getForOwner(type: String, id: Int): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AttachmentEntity>>

    @Insert
    suspend fun insert(entity: AttachmentEntity): Long

    @Update
    suspend fun update(entity: AttachmentEntity)

    @Delete
    suspend fun delete(entity: AttachmentEntity)
}