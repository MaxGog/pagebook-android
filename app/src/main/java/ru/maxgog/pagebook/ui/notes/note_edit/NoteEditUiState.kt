package ru.maxgog.pagebook.ui.notes.note_edit

import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.model.Tag

sealed interface NoteEditUiState {
    data object Loading : NoteEditUiState
    data class NewNote(val selectedTags: List<Tag> = emptyList()) : NoteEditUiState
    data class Editing(val note: Note, val tags: List<Tag> = emptyList()) : NoteEditUiState
    data class Error(val message: String) : NoteEditUiState
}