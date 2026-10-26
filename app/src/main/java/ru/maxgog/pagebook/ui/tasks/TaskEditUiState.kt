package ru.maxgog.pagebook.ui.tasks

import ru.maxgog.pagebook.domain.model.Task

sealed interface TaskEditUiState {
    data object Loading : TaskEditUiState
    data object NewTask : TaskEditUiState
    data class Editing(val task: Task) : TaskEditUiState
    data class Error(val message: String) : TaskEditUiState
}