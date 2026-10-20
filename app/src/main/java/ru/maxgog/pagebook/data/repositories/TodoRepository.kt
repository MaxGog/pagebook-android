package ru.maxgog.pagebook.data.repositories

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.dao.TodoDao
import ru.maxgog.pagebook.data.models.TodoModel

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoModel>> = todoDao.getAllTodos()

    suspend fun insert(todo: TodoModel) = todoDao.insert(todo)
    suspend fun update(todo: TodoModel) = todoDao.update(todo)
    suspend fun delete(todo: TodoModel) = todoDao.delete(todo)
}