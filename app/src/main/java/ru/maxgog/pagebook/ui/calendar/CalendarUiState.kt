package ru.maxgog.pagebook.ui.calendar

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.maxgog.pagebook.domain.model.Event

data class CalendarUiState(
    val isLoading: Boolean = true,
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val eventsForSelectedDate: List<Event> = emptyList(),
    val showAddDialog: Boolean = false,
    val error: String? = null
)