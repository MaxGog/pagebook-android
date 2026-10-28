package ru.maxgog.pagebook.ui.tasks

import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.Task

sealed interface TaskEditUiState {
    data object Loading : TaskEditUiState
    data class NewTask(val selectedTags: List<Tag> = emptyList()) : TaskEditUiState
    data class Editing(val task: Task, val tags: List<Tag> = emptyList()) : TaskEditUiState
    data class Error(val message: String) : TaskEditUiState
}