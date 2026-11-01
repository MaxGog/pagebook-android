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
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.core.notification.AppNotificationManager
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.model.TagFilterModule
import ru.maxgog.pagebook.domain.model.TagFilterState
import ru.maxgog.pagebook.domain.repository.TagFilterRepository
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.usecase.event.DeleteEventUseCase
import ru.maxgog.pagebook.domain.usecase.event.GetAllEventsUseCase
import ru.maxgog.pagebook.domain.usecase.event.SaveEventUseCase
import ru.maxgog.pagebook.domain.usecase.tag.CreateTagUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.util.applyTagFilter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getAllEvents: GetAllEventsUseCase,
    private val saveEvent: SaveEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    getAllTags: GetAllTagsUseCase,
    private val createTag: CreateTagUseCase,
    private val tagRepository: TagRepository,
    private val tagFilterRepository: TagFilterRepository,
    private val notificationManager: AppNotificationManager
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
    private val _showDialog = MutableStateFlow(false)
    private val _pendingTags = MutableStateFlow<List<Tag>>(emptyList())

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pendingTags: StateFlow<List<Tag>> = _pendingTags.asStateFlow()

    val filterState: StateFlow<TagFilterState> = tagFilterRepository
        .getFilter(TagFilterModule.CALENDAR)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TagFilterState())

    val uiState: StateFlow<CalendarUiState> = combine(
        getAllEvents(),
        _selectedDate,
        _showDialog,
        filterState
    ) { events, date, show, filter ->
        val filtered = events.applyTagFilter(filter) { it.tags }
        CalendarUiState(
            isLoading = false,
            selectedDate = date,
            eventsForSelectedDate = filtered.filter { it.date == date },
            showAddDialog = show,
            datesWithEvents = filtered.map { it.date }.toSet() as Set<LocalDate>
        )
    }
        .catch { emit(CalendarUiState(error = it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    fun onDateSelected(date: LocalDate) { _selectedDate.value = date }
    fun showAddDialog() { _showDialog.value = true }

    fun hideAddDialog() {
        _showDialog.value = false
        _pendingTags.value = emptyList()
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
            try {
                val eventId = saveEvent(
                    Event(title = title, description = description, date = date, time = time)
                )
                _pendingTags.value.forEach { tag ->
                    tagRepository.addTagToEvent(eventId.toInt(), tag.id)
                }
                _showDialog.value = false
                _pendingTags.value = emptyList()
                notificationManager.success(R.string.event_added)
            } catch (e: Exception) {
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            try {
                deleteEventUseCase(event)
                notificationManager.success(R.string.event_deleted)
            } catch (e: Exception) {
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun toggleTagFilter(tagId: Int) = viewModelScope.launch {
        val cur = filterState.value
        val newIds = if (tagId in cur.tagIds) cur.tagIds - tagId else cur.tagIds + tagId
        tagFilterRepository.setFilter(TagFilterModule.CALENDAR, cur.copy(tagIds = newIds))
    }

    fun toggleFilterMode() = viewModelScope.launch {
        val cur = filterState.value
        val newMode = if (cur.mode == FilterMode.OR) FilterMode.AND else FilterMode.OR
        tagFilterRepository.setFilter(TagFilterModule.CALENDAR, cur.copy(mode = newMode))
    }

    fun clearTagFilter() = viewModelScope.launch {
        tagFilterRepository.clearFilter(TagFilterModule.CALENDAR)
    }
}