package ru.maxgog.pagebook.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.usecase.GetAllNotesUseCase
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNotesUseCase: GetAllNotesUseCase
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = getAllNotesUseCase()
        .map<List<Note>, NotesUiState> {
            NotesUiState.Success(it)
        }
        .catch { emit(NotesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState.Loading
        )
}