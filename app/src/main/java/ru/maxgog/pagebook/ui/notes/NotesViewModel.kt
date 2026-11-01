package ru.maxgog.pagebook.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagFilterModule
import ru.maxgog.pagebook.domain.model.TagFilterState
import ru.maxgog.pagebook.domain.repository.TagFilterRepository
import ru.maxgog.pagebook.domain.usecase.note.GetAllNotesUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.util.applyTagFilter
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNotesUseCase: GetAllNotesUseCase,
    getAllTags: GetAllTagsUseCase,
    private val tagFilterRepository: TagFilterRepository
) : ViewModel() {

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filterState: StateFlow<TagFilterState> = tagFilterRepository
        .getFilter(TagFilterModule.NOTES)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TagFilterState())

    val uiState: StateFlow<NotesUiState> = combine<List<Note>, TagFilterState, NotesUiState>(
        getAllNotesUseCase(),
        filterState
    ) { notes, filter ->
        NotesUiState.Success(notes.applyTagFilter(filter) { it.tags })
    }
        .catch { emit(NotesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState.Loading)

    fun toggleTagFilter(tagId: Int) = viewModelScope.launch {
        val cur = filterState.value
        val newIds = if (tagId in cur.tagIds) cur.tagIds - tagId else cur.tagIds + tagId
        tagFilterRepository.setFilter(TagFilterModule.NOTES, cur.copy(tagIds = newIds))
    }

    fun toggleFilterMode() = viewModelScope.launch {
        val cur = filterState.value
        val newMode = if (cur.mode == FilterMode.OR) FilterMode.AND else FilterMode.OR
        tagFilterRepository.setFilter(TagFilterModule.NOTES, cur.copy(mode = newMode))
    }

    fun clearTagFilter() = viewModelScope.launch {
        tagFilterRepository.clearFilter(TagFilterModule.NOTES)
    }
}