package ru.maxgog.pagebook.ui.note_edit

import ru.maxgog.pagebook.domain.model.Note

sealed interface NoteEditUiState {
    data object Loading : NoteEditUiState
    data object NewNote : NoteEditUiState
    data class Editing(val note: Note) : NoteEditUiState
    data class Error(val message: String) : NoteEditUiState
}