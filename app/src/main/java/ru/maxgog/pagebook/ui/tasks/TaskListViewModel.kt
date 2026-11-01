package ru.maxgog.pagebook.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.core.notification.AppNotificationManager
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.usecase.task.DeleteTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.GetAllTasksUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    getAllTasks: GetAllTasksUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    getAllTags: GetAllTagsUseCase,
    private val notificationManager: AppNotificationManager
) : ViewModel() {

    private val _selectedTagIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTagIds: StateFlow<Set<Int>> = _selectedTagIds.asStateFlow()

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState: StateFlow<TaskListUiState> = combine<
            List<Task>,
            Set<Int>,
            TaskListUiState
            >(getAllTasks(), _selectedTagIds) { tasks, tagIds ->
        val filtered = if (tagIds.isEmpty()) tasks
        else tasks.filter { task -> task.tags.any { it.id in tagIds } }
        TaskListUiState.Success(filtered)
    }
        .catch { emit(TaskListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskListUiState.Loading)

    fun toggleCompleted(task: Task) {
        viewModelScope.launch { saveTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            try {
                deleteTask(task)
                notificationManager.success(R.string.task_deleted)
            } catch (e: Exception) {
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun toggleTagFilter(tagId: Int) {
        _selectedTagIds.update { ids -> if (tagId in ids) ids - tagId else ids + tagId }
    }

    fun clearTagFilter() { _selectedTagIds.value = emptySet() }
}