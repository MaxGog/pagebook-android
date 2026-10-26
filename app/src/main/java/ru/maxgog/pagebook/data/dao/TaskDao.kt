package ru.maxgog.pagebook.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.TaskModel

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY IsCompleted ASC, AtCreated DESC")
    fun getAllTasks(): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks WHERE NoteId = :noteId")
    fun getTasksByNoteId(noteId: Int): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Int): TaskModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskModel): Long

    @Update
    suspend fun update(task: TaskModel)

    @Delete
    suspend fun delete(task: TaskModel)
}