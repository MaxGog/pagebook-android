package ru.maxgog.pagebook.ui.tasks

import ru.maxgog.pagebook.domain.model.Task

sealed interface TaskListUiState {
    data object Loading : TaskListUiState
    data class Success(val tasks: List<Task>) : TaskListUiState
    data class Error(val message: String) : TaskListUiState
}