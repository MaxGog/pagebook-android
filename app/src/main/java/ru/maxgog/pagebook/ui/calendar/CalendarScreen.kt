package ru.maxgog.pagebook.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.toKotlinxLocalDate
import ru.maxgog.pagebook.ui.app.EmptyState
import ru.maxgog.pagebook.ui.components.ContextAction
import ru.maxgog.pagebook.ui.components.ContextualItem
import ru.maxgog.pagebook.ui.components.FilterBadgeIcon
import ru.maxgog.pagebook.ui.components.OverflowMenu
import ru.maxgog.pagebook.ui.components.TagFilterRow
import ru.maxgog.pagebook.ui.theme.AppTheme
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onSettingsClick: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val pendingTags by viewModel.pendingTags.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var filterRowVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(filterState.count) {
        if (filterState.count > 0) filterRowVisible = true
    }

    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth
    )

    LaunchedEffect(uiState.selectedDate) {
        calendarState.animateScrollToMonth(
            YearMonth.of(uiState.selectedDate.year, uiState.selectedDate.monthNumber)
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.calendar),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    FilterBadgeIcon(
                        count = filterState.count,
                        onClick = { filterRowVisible = !filterRowVisible }
                    )
                    OverflowMenu(onSettingsClick = onSettingsClick)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        text = stringResource(R.string.add),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.error != null -> EmptyState(
                text = uiState.error ?: "",
                modifier = Modifier.padding(innerPadding)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedVisibility(
                    visible = filterRowVisible && allTags.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    TagFilterRow(
                        allTags = allTags,
                        selectedTagIds = filterState.tagIds,
                        mode = filterState.mode,
                        onToggleTag = viewModel::toggleTagFilter,
                        onToggleMode = viewModel::toggleFilterMode,
                        onClearFilters = viewModel::clearTagFilter,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                VerticalCalendar(
                    state = calendarState,
                    modifier = Modifier.weight(1f),
                    dayContent = { day ->
                        val dayDate = day.date.toKotlinxLocalDate()
                        DayCell(
                            day = day,
                            isSelected = dayDate == uiState.selectedDate,
                            hasEvents = dayDate in uiState.datesWithEvents,
                            onClick = { viewModel.onDateSelected(dayDate) }
                        )
                    },
                    monthHeader = { month -> MonthHeader(month.yearMonth) }
                )

                EventsList(
                    events = uiState.eventsForSelectedDate,
                    emptyText = if (filterState.isEmpty)
                        stringResource(R.string.no_events)
                    else
                        stringResource(R.string.no_matching_items),
                    onEdit = viewModel::startEdit,
                    onRequestDelete = { eventToDelete = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (uiState.showAddDialog) {
        val editing = uiState.editingEvent
        AddEventDialog(
            date = editing?.date ?: uiState.selectedDate,
            initialEvent = editing,
            allTags = allTags,
            selectedTags = pendingTags,
            onToggleTag = viewModel::toggleTag,
            onCreateTag = viewModel::createAndToggleTag,
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { title, description, date, time ->
                viewModel.saveEvent(title, description, date, time)
            }
        )
    }

    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_event_confirm, event.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(event)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun EventsList(
    events: List<Event>,
    emptyText: String,
    onEdit: (Event) -> Unit,
    onRequestDelete: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    if (events.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events, key = { it.id }) { event ->
                val actions = rememberEventActions(
                    onEdit = { onEdit(event) },
                    onDelete = { onRequestDelete(event) }
                )
                ContextualItem(
                    actions = actions,
                    onClick = { onEdit(event) }
                ) {
                    EventItem(event = event)
                }
            }
        }
    } else {
        EmptyState(text = emptyText, modifier = modifier)
    }
}

@Composable
private fun rememberEventActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit
): List<ContextAction> {
    val editLabel = stringResource(R.string.edit_event)
    val deleteLabel = stringResource(R.string.delete)
    return remember(editLabel, deleteLabel) {
        listOf(
            ContextAction(
                label = editLabel,
                icon = Icons.Default.Edit,
                onClick = onEdit
            ),
            ContextAction(
                label = deleteLabel,
                icon = Icons.Default.Delete,
                isDestructive = true,
                onClick = onDelete
            )
        )
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth) {
    val locale = LocalConfiguration.current.locales[0]
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.horizontalPadding,
                vertical = AppTheme.verticalPadding
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                .replaceFirstChar { it.titlecase(locale) } + " " + yearMonth.year,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(AppTheme.horizontalPadding)
        )
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
            if (hasEvents) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}