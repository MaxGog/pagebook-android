package ru.maxgog.pagebook.ui.calendar

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
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.usecase.event.DeleteEventUseCase
import ru.maxgog.pagebook.domain.usecase.event.GetAllEventsUseCase
import ru.maxgog.pagebook.domain.usecase.event.SaveEventUseCase
import ru.maxgog.pagebook.domain.usecase.tag.CreateTagUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getAllEvents: GetAllEventsUseCase,
    private val saveEvent: SaveEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    getAllTags: GetAllTagsUseCase,
    private val createTag: CreateTagUseCase,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
    private val _showDialog = MutableStateFlow(false)
    private val _pendingTags = MutableStateFlow<List<Tag>>(emptyList())

    // Все теги для picker'а
    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Выбранные теги для нового события
    val pendingTags: StateFlow<List<Tag>> = _pendingTags.asStateFlow()

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

    fun onDateSelected(date: LocalDate) { _selectedDate.value = date }

    fun showAddDialog() { _showDialog.value = true }

    fun hideAddDialog() {
        _showDialog.value = false
        _pendingTags.value = emptyList()   // сбрасываем при отмене
    }

    fun toggleTag(tag: Tag) {
        _pendingTags.update { tags ->
            if (tags.any { it.id == tag.id }) tags - tag else tags + tag
        }
    }

    fun createAndToggleTag(name: String, color: TagColor, emoji: String? = null) {
        viewModelScope.launch {
            val tagId = createTag(name, color, emoji)
            val tag = Tag(
                id = tagId.toInt(),
                name = name.trim(),
                colorHex = color.hex,
                emoji = emoji
            )
            toggleTag(tag)
        }
    }

    fun addEvent(title: String, description: String, date: LocalDate, time: String) {
        viewModelScope.launch {
            val eventId = saveEvent(
                Event(
                    title = title,
                    description = description,
                    date = date,
                    time = time
                )
            )

            _pendingTags.value.forEach { tag ->
                tagRepository.addTagToEvent(eventId.toInt(), tag.id)
            }

            _showDialog.value = false
            _pendingTags.value = emptyList()
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch { deleteEventUseCase(event) }
    }
}