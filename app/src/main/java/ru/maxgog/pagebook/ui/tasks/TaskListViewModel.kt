package ru.maxgog.pagebook.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.usecase.task.DeleteTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.GetAllTasksUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    getAllTasks: GetAllTasksUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    val uiState: StateFlow<TaskListUiState> = getAllTasks()
        .map<List<Task>, TaskListUiState> { TaskListUiState.Success(it) }
        .catch { emit(TaskListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskListUiState.Loading)

    fun toggleCompleted(task: Task) {
        viewModelScope.launch { saveTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun delete(task: Task) {
        viewModelScope.launch { deleteTask(task) }
    }
}