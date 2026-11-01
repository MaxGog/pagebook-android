package ru.maxgog.pagebook.ui.tasks

import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.Task

sealed interface TaskEditUiState {
    data object Loading : TaskEditUiState

    data class NewTask(
        val selectedTags: List<Tag> = emptyList(),
        val pendingAttachments: List<Attachment> = emptyList()
    ) : TaskEditUiState

    data class Editing(
        val task: Task,
        val tags: List<Tag> = emptyList(),
        val attachments: List<Attachment> = emptyList()
    ) : TaskEditUiState

    data class Error(val message: String) : TaskEditUiState
}

sealed interface TaskEditEvent {
    data object Saved : TaskEditEvent
    data object Deleted : TaskEditEvent
}