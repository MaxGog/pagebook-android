package ru.maxgog.pagebook.ui.tasks

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.OwnerType
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.usecase.attachment.DeleteAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.DownloadAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.GetAttachmentsForTaskUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.SaveAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.tag.CreateTagUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetTagsForTaskUseCase
import ru.maxgog.pagebook.domain.usecase.tag.ToggleTagOnTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.DeleteTaskUseCase
import ru.maxgog.pagebook.domain.usecase.task.GetTaskByIdUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskById: GetTaskByIdUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    getAllTags: GetAllTagsUseCase,
    private val getTagsForTask: GetTagsForTaskUseCase,
    private val toggleTagOnTask: ToggleTagOnTaskUseCase,
    private val createTag: CreateTagUseCase,
    private val tagRepository: TagRepository,
    private val saveAttachment: SaveAttachmentUseCase,
    private val getAttachmentsForTask: GetAttachmentsForTaskUseCase,
    private val deleteAttachment: DeleteAttachmentUseCase,
    private val downloadAttachmentUseCase: DownloadAttachmentUseCase,
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {

    private var taskId: Int? = savedStateHandle.get<Int>("taskId")?.takeIf { it != 0 }

    private val _uiState = MutableStateFlow<TaskEditUiState>(TaskEditUiState.Loading)
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<TaskEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _snackbarMessageRes = MutableStateFlow<Int?>(null)
    val snackbarMessageRes: StateFlow<Int?> = _snackbarMessageRes.asStateFlow()

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var saved = false

    init { load() }

    fun setTaskId(id: Int) {
        if (taskId == id) return
        taskId = id
        load()
    }

    private fun load() {
        val id = taskId
        viewModelScope.launch {
            _uiState.value = if (id == null) {
                TaskEditUiState.NewTask()
            } else {
                val task = getTaskById(id)
                if (task == null) TaskEditUiState.Error("Task not found")
                else TaskEditUiState.Editing(task = task, tags = task.tags)
            }
        }
        if (id != null) {
            viewModelScope.launch {
                getTagsForTask(id).collect { tags ->
                    val current = _uiState.value
                    if (current is TaskEditUiState.Editing) {
                        _uiState.value = current.copy(tags = tags)
                    }
                }
            }
            viewModelScope.launch {
                getAttachmentsForTask(id).collect { attachments ->
                    val current = _uiState.value
                    if (current is TaskEditUiState.Editing) {
                        _uiState.value = current.copy(attachments = attachments)
                    }
                }
            }
        }
    }

    fun attachUri(uri: Uri) {
        viewModelScope.launch {
            try {
                when (val current = _uiState.value) {
                    is TaskEditUiState.Editing -> {
                        saveAttachment(uri, OwnerType.TASK, current.task.id)
                    }
                    is TaskEditUiState.NewTask -> {
                        val pending = saveAttachment(uri, OwnerType.TASK, 0)
                        _uiState.value = current.copy(
                            pendingAttachments = current.pendingAttachments + pending
                        )
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                Log.e(TAG, "attachUri failed", e)
                _snackbarMessageRes.value = R.string.attachment_save_error
            }
        }
    }

    fun removeAttachment(attachment: Attachment) {
        val current = _uiState.value
        when (current) {
            is TaskEditUiState.Editing -> viewModelScope.launch {
                deleteAttachment(attachment)
            }
            is TaskEditUiState.NewTask -> viewModelScope.launch {
                deleteAttachment(attachment)
                _uiState.value = current.copy(
                    pendingAttachments = current.pendingAttachments - attachment
                )
            }
            else -> Unit
        }
    }

    fun downloadAttachment(attachment: Attachment) {
        viewModelScope.launch {
            try {
                downloadAttachmentUseCase(attachment)
                _snackbarMessageRes.value = R.string.downloaded
            } catch (e: Exception) {
                Log.e(TAG, "downloadAttachment failed", e)
                _snackbarMessageRes.value = R.string.download_error
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessageRes.value = null
    }

    fun toggleTag(tag: Tag) {
        val current = _uiState.value
        when (current) {
            is TaskEditUiState.Editing -> viewModelScope.launch {
                toggleTagOnTask(current.task.id, tag)
            }
            is TaskEditUiState.NewTask -> {
                val newTags = if (current.selectedTags.any { it.id == tag.id }) {
                    current.selectedTags - tag
                } else current.selectedTags + tag
                _uiState.value = current.copy(selectedTags = newTags)
            }
            else -> Unit
        }
    }

    fun createAndToggleTag(name: String, color: TagColor, emoji: String? = null) {
        viewModelScope.launch {
            val tagId = createTag(name, color, emoji)
            val tag = Tag(id = tagId.toInt(), name = name.trim(), colorHex = color.hex, emoji = emoji)
            toggleTag(tag)
        }
    }

    fun save(title: String, description: String, reminderTime: Long?, noteId: Int? = null) {
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

            try {
                val savedId = saveTask(task)

                if (current is TaskEditUiState.NewTask) {
                    current.selectedTags.forEach { tag ->
                        tagRepository.addTagToTask(savedId.toInt(), tag.id)
                    }
                    // Pending-вложения уже в БД (id != 0, ownerId = 0)
                    // → UPDATE, а не повторный INSERT.
                    current.pendingAttachments.forEach { pending ->
                        attachmentRepository.updateAttachment(
                            pending.copy(ownerId = savedId.toInt())
                        )
                    }
                }

                saved = true
                _events.send(TaskEditEvent.Saved)
            } catch (e: Exception) {
                Log.e(TAG, "save task failed", e)
                _snackbarMessageRes.value = R.string.unknown_error
            }
        }
    }

    fun delete() {
        val current = _uiState.value
        if (current is TaskEditUiState.Editing) {
            viewModelScope.launch {
                current.attachments.forEach { deleteAttachment(it) }
                deleteTask(current.task)
                _events.send(TaskEditEvent.Deleted)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!saved) {
            val pending = (_uiState.value as? TaskEditUiState.NewTask)?.pendingAttachments.orEmpty()
            if (pending.isNotEmpty()) {
                kotlinx.coroutines.GlobalScope.launch {
                    pending.forEach { runCatching { deleteAttachment(it) } }
                }
            }
        }
    }

    private companion object {
        const val TAG = "TaskEditViewModel"
    }
}