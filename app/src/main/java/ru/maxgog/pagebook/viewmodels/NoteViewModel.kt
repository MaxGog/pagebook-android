package ru.maxgog.pagebook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.data.repositories.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    val allNotes = repository.allNotes

    fun insert(note: NoteModel) = viewModelScope.launch { repository.insert(note) }

    fun update(note: NoteModel) = viewModelScope.launch { repository.update(note) }

    fun delete(note: NoteModel) = viewModelScope.launch { repository.delete(note) }
}


