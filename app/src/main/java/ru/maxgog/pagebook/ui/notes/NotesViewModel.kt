package ru.maxgog.pagebook.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.usecase.note.GetAllNotesUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNotesUseCase: GetAllNotesUseCase,
    getAllTags: GetAllTagsUseCase
) : ViewModel() {

    private val _selectedTagIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTagIds: StateFlow<Set<Int>> = _selectedTagIds.asStateFlow()

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<NotesUiState> = combine<
            List<Note>,
            Set<Int>,
            NotesUiState
            >(getAllNotesUseCase(), _selectedTagIds) { notes, tagIds ->
        val filtered = if (tagIds.isEmpty()) notes
        else notes.filter { note -> note.tags.any { it.id in tagIds } }
        NotesUiState.Success(filtered)
    }
        .catch { emit(NotesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState.Loading
        )

    fun toggleTagFilter(tagId: Int) {
        _selectedTagIds.update { ids -> if (tagId in ids) ids - tagId else ids + tagId }
    }

    fun clearTagFilter() {
        _selectedTagIds.value = emptySet()
    }
}