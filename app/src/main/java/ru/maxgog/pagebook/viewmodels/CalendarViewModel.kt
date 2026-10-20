package ru.maxgog.pagebook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.composecalendar.kotlinxDateTime.now
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ru.maxgog.pagebook.PageBookApplication
import ru.maxgog.pagebook.data.models.EventModel
import ru.maxgog.pagebook.data.repositories.EventRepository

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _events = MutableStateFlow(emptyList<EventModel>())
    val events = _events.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _dialogDate = MutableStateFlow(LocalDate.now())
    val dialogDate = _dialogDate.asStateFlow()

    init {
        loadEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun showAddEventDialog(date: LocalDate) {
        _dialogDate.value = date
        _showDialog.value = true
    }

    fun hideAddEventDialog() {
        _showDialog.value = false
    }

    fun addEvent(event: EventModel) = viewModelScope.launch {
        repository.insert(event)
        loadEvents()
    }

    fun deleteEvent(event: EventModel) = viewModelScope.launch {
        repository.delete(event)
        loadEvents()
    }

    private fun loadEvents() = viewModelScope.launch {
        _events.value = repository.getAllEvents()
    }
}