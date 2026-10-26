package ru.maxgog.pagebook.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.usecase.event.DeleteEventUseCase
import ru.maxgog.pagebook.domain.usecase.event.GetAllEventsUseCase
import ru.maxgog.pagebook.domain.usecase.event.SaveEventUseCase
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getAllEvents: GetAllEventsUseCase,
    private val saveEvent: SaveEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
    private val _showDialog = MutableStateFlow(false)

    val uiState: StateFlow<CalendarUiState> =
        combine(getAllEvents(), _selectedDate, _showDialog) { events, date, show ->
            CalendarUiState(
                isLoading = false,
                selectedDate = date,
                eventsForSelectedDate = events.filter { it.date == date },
                showAddDialog = show
            )
        }
            .catch { emit(CalendarUiState(error = it.message ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CalendarUiState()
            )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun showAddDialog() { _showDialog.value = true }
    fun hideAddDialog() { _showDialog.value = false }

    fun addEvent(title: String, description: String, date: LocalDate, time: String) {
        viewModelScope.launch {
            saveEvent(
                Event(
                    title = title,
                    description = description,
                    date = date,
                    time = time
                )
            )
            _showDialog.value = false
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch { deleteEventUseCase(event) }
    }
}