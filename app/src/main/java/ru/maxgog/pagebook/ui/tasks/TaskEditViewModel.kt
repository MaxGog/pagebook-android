package ru.maxgog.pagebook.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.usecase.task.DeleteTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.GetTaskByIdUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskById: GetTaskByIdUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    private val taskId: Int? = savedStateHandle.get<Int>("taskId")

    private val _uiState = MutableStateFlow<TaskEditUiState>(TaskEditUiState.Loading)
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = if (taskId == null || taskId == 0) {
                TaskEditUiState.NewTask
            } else {
                getTaskById(taskId)?.let { TaskEditUiState.Editing(it) }
                    ?: TaskEditUiState.Error("Задача не найдена")
            }
        }
    }

    fun save(
        title: String,
        description: String,
        reminderTime: Long?,
        noteId: Int? = null
    ) {
        viewModelScope.launch {
            val current = _uiState.value
            val task = when (current) {
                is TaskEditUiState.Editing -> current.task.copy(
                    title = title,
                    description = description.ifBlank { null },
                    hasReminder = reminderTime != null,
                    reminderTime = reminderTime,
                    noteId = noteId ?: current.task.noteId
                )
                else -> Task(
                    title = title,
                    description = description.ifBlank { null },
                    hasReminder = reminderTime != null,
                    reminderTime = reminderTime,
                    noteId = noteId
                )
            }
            saveTask(task)
        }
    }

    fun delete() {
        val current = _uiState.value
        if (current is TaskEditUiState.Editing) {
            viewModelScope.launch { deleteTask(current.task) }
        }
    }
}