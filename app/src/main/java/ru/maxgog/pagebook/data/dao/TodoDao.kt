package ru.maxgog.pagebook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.TodoModel

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAllTodos(): Flow<List<TodoModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoModel)

    @Update
    suspend fun update(todo: TodoModel)

    @Delete
    suspend fun delete(todo: TodoModel)
}