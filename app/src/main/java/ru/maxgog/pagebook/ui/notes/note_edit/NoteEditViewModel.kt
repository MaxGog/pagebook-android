package ru.maxgog.pagebook.ui.notes.note_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.usecase.note.DeleteNoteUseCase
import ru.maxgog.pagebook.domain.usecase.note.GetNoteByIdUseCase
import ru.maxgog.pagebook.domain.usecase.note.SaveNoteUseCase
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase
) : ViewModel() {

    private val noteId: Int? = savedStateHandle.get<Int>("noteId")

    private val _uiState = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = if (noteId == null) {
                NoteEditUiState.NewNote
            } else {
                getNoteById(noteId)?.let { NoteEditUiState.Editing(it) }
                    ?: NoteEditUiState.Error("Note not found")
            }
        }
    }

    fun save(title: String, content: String) {
        viewModelScope.launch {
            val current = _uiState.value
            val note = when (current) {
                is NoteEditUiState.Editing -> current.note.copy(title = title, content = content)
                else -> Note(title = title, content = content)
            }
            saveNote(note)
        }
    }

    fun delete() {
        val current = _uiState.value
        if (current is NoteEditUiState.Editing) {
            viewModelScope.launch { deleteNote(current.note) }
        }
    }
}