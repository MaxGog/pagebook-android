package ru.maxgog.pagebook.ui.screens.notes

import ru.maxgog.pagebook.domain.model.Note

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(val notes: List<Note>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}