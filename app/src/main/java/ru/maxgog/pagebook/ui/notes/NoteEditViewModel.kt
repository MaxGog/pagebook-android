package ru.maxgog.pagebook.ui.notes

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
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.usecase.note.DeleteNoteUseCase
import ru.maxgog.pagebook.domain.usecase.note.GetNoteByIdUseCase
import ru.maxgog.pagebook.domain.usecase.note.SaveNoteUseCase
import ru.maxgog.pagebook.domain.usecase.tag.CreateTagUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetTagsForNoteUseCase
import ru.maxgog.pagebook.domain.usecase.tag.ToggleTagOnNoteUseCase
import ru.maxgog.pagebook.domain.usecase.task.CreateTaskFromNoteUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    getAllTags: GetAllTagsUseCase,
    private val getTagsForNote: GetTagsForNoteUseCase,
    private val toggleTagOnNote: ToggleTagOnNoteUseCase,
    private val createTag: CreateTagUseCase,
    private val tagRepository: TagRepository,
    private val createTaskFromNote: CreateTaskFromNoteUseCase,
    private val saveTask: SaveTaskUseCase
) : ViewModel() {

    private val noteId: Int? = savedStateHandle.get<Int>("noteId")

    private val _uiState = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<NoteEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _taskCreating = MutableStateFlow(false)
    val taskCreating: StateFlow<Boolean> = _taskCreating.asStateFlow()

    private val _snackbarMessageRes = MutableStateFlow<Int?>(null)
    val snackbarMessageRes: StateFlow<Int?> = _snackbarMessageRes.asStateFlow()

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            _uiState.value = if (noteId == null) {
                NoteEditUiState.NewNote()
            } else {
                val note = getNoteById(noteId)
                if (note == null) NoteEditUiState.Error("Note not found")
                else NoteEditUiState.Editing(note = note, tags = note.tags)
            }
        }

        noteId?.let { id ->
            viewModelScope.launch {
                getTagsForNote(id).collect { tags ->
                    val current = _uiState.value
                    if (current is NoteEditUiState.Editing) {
                        _uiState.value = current.copy(tags = tags)
                    }
                }
            }
        }
    }

    fun toggleTag(tag: Tag) {
        val current = _uiState.value
        when (current) {
            is NoteEditUiState.Editing -> viewModelScope.launch {
                toggleTagOnNote(current.note.id, tag)
            }
            is NoteEditUiState.NewNote -> {
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

    fun save(title: String, content: String) {
        viewModelScope.launch {
            val current = _uiState.value
            val note = when (current) {
                is NoteEditUiState.Editing -> current.note.copy(title = title, content = content)
                else -> Note(title = title, content = content)
            }

            try {
                val savedId = saveNote(note)
                if (current is NoteEditUiState.NewNote) {
                    current.selectedTags.forEach { tag ->
                        tagRepository.addTagToNote(savedId.toInt(), tag.id)
                    }
                }
                _events.send(NoteEditEvent.Saved)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun delete() {
        val current = _uiState.value
        if (current is NoteEditUiState.Editing) {
            viewModelScope.launch {
                deleteNote(current.note)
                _events.send(NoteEditEvent.Deleted)
            }
        }
    }

    fun createTaskFromCurrentNote() {
        val current = _uiState.value as? NoteEditUiState.Editing ?: return

        viewModelScope.launch {
            _taskCreating.value = true
            try {
                val task = createTaskFromNote(
                    noteContent = current.note.content,
                    noteTitle = current.note.title
                )
                saveTask(task.copy(noteId = current.note.id))

                _snackbarMessageRes.value = if (task.hasReminder)
                    R.string.task_created_with_reminder
                else
                    R.string.task_created
            } catch (e: Exception) {
                _snackbarMessageRes.value = R.string.task_creation_error
            } finally {
                _taskCreating.value = false
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessageRes.value = null
    }
}