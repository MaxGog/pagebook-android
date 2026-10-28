package ru.maxgog.pagebook.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.toKotlinxLocalDate
import ru.maxgog.pagebook.ui.EmptyState
import ru.maxgog.pagebook.ui.theme.AppTheme
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val pendingTags by viewModel.pendingTags.collectAsStateWithLifecycle()

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth
    )

    // При смене выбранной даты — скроллим календарь к её месяцу
    LaunchedEffect(uiState.selectedDate) {
        calendarState.animateScrollToMonth(
            YearMonth.of(uiState.selectedDate.year, uiState.selectedDate.monthNumber)
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.add))
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                VerticalCalendar(
                    state = calendarState,
                    modifier = Modifier.weight(1f),
                    dayContent = { day ->
                        val dayEvents = remember(day.date, uiState.eventsForSelectedDate) {
                            if (day.date == uiState.selectedDate) uiState.eventsForSelectedDate
                            else emptyList()
                        }
                        DayCell(
                            day = day,
                            isSelected = day.date == uiState.selectedDate,
                            hasEvents = dayEvents.isNotEmpty(),
                            onClick = { viewModel.onDateSelected(day.date.toKotlinxLocalDate()) }
                        )
                    },
                    monthHeader = { month -> MonthHeader(month.yearMonth) }
                )

                EventsList(
                    events = uiState.eventsForSelectedDate,
                    onDelete = viewModel::deleteEvent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (uiState.showAddDialog) {
        AddEventDialog(
            date = uiState.selectedDate,
            allTags = allTags,
            selectedTags = pendingTags,
            onToggleTag = viewModel::toggleTag,
            onCreateTag = viewModel::createAndToggleTag,
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { title, description, date, time ->
                viewModel.addEvent(title, description, date, time)
            }
        )
    }
}

@Composable
private fun EventsList(
    events: List<Event>,
    onDelete: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    if (events.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(AppTheme.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.itemSpacing)
        ) {
            items(events, key = { it.id }) { event ->
                EventItem(
                    event = event,
                    onDelete = { onDelete(event) },
                    modifier = Modifier.padding(horizontal = AppTheme.horizontalPadding)
                )
            }
        }
    } else {
        EmptyState(
            text = stringResource(R.string.no_events),
            modifier = modifier
        )
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth) {
    val locale = LocalConfiguration.current.locales[0]
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.horizontalPadding, vertical = AppTheme.verticalPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                .replaceFirstChar { it.titlecase(locale) } + " " + yearMonth.year,
            style = MaterialTheme.typography.titleLarge,
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
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}