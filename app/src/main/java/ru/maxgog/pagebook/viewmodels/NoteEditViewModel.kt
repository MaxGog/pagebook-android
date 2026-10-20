package ru.maxgog.pagebook.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.repositories.NoteRepository
import ru.maxgog.pagebook.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Int? = savedStateHandle.get<Int>(Routes.ARG_NOTE_ID)

    private val _note = MutableStateFlow<NoteModel?>(null)
    val note: StateFlow<NoteModel?> = _note.asStateFlow()

    val isNewNote: Boolean = noteId == null

    init {
        if (noteId != null) {
            viewModelScope.launch {
                repository.getNoteById(noteId).collect { _note.value = it }
            }
        }
    }

    fun save(title: String, content: String) {
        viewModelScope.launch {
            val existing = _note.value
            if (existing != null) {
                repository.update(existing.copy(title = title, content = content))
            } else {
                repository.insert(NoteModel(title = title, content = content))
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            _note.value?.let { repository.delete(it) }
        }
    }
}