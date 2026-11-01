package ru.maxgog.pagebook.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.core.notification.AppNotificationManager
import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagFilterModule
import ru.maxgog.pagebook.domain.model.TagFilterState
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.TagFilterRepository
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.usecase.task.DeleteTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.GetAllTasksUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import ru.maxgog.pagebook.domain.util.applyTagFilter
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    getAllTasks: GetAllTasksUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    getAllTags: GetAllTagsUseCase,
    private val tagFilterRepository: TagFilterRepository,
    private val notificationManager: AppNotificationManager
) : ViewModel() {

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filterState: StateFlow<TagFilterState> = tagFilterRepository
        .getFilter(TagFilterModule.TASKS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TagFilterState())

    val uiState: StateFlow<TaskListUiState> = combine<List<Task>, TagFilterState, TaskListUiState>(
        getAllTasks(),
        filterState
    ) { tasks, filter ->
        TaskListUiState.Success(tasks.applyTagFilter(filter) { it.tags })
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

    fun toggleTagFilter(tagId: Int) = viewModelScope.launch {
        val cur = filterState.value
        val newIds = if (tagId in cur.tagIds) cur.tagIds - tagId else cur.tagIds + tagId
        tagFilterRepository.setFilter(TagFilterModule.TASKS, cur.copy(tagIds = newIds))
    }

    fun toggleFilterMode() = viewModelScope.launch {
        val cur = filterState.value
        val newMode = if (cur.mode == FilterMode.OR) FilterMode.AND else FilterMode.OR
        tagFilterRepository.setFilter(TagFilterModule.TASKS, cur.copy(mode = newMode))
    }

    fun clearTagFilter() = viewModelScope.launch {
        tagFilterRepository.clearFilter(TagFilterModule.TASKS)
    }
}