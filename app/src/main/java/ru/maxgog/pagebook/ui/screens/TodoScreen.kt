package ru.maxgog.pagebook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.data.models.TodoModel
import ru.maxgog.pagebook.ui.EmptyState
import ru.maxgog.pagebook.ui.dialogs.AddTodoDialog
import ru.maxgog.pagebook.ui.dialogs.DateTimePickerDialog
import ru.maxgog.pagebook.ui.items.TodoItem
import ru.maxgog.pagebook.ui.theme.AppTheme
import ru.maxgog.pagebook.viewmodels.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
) {
    val todos by viewModel.allTodos.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var newTodoTitle by remember { mutableStateOf("") }
    var newTodoDescription by remember { mutableStateOf("") }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.todo_list)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.add_task))
            }
        },
    ) { padding ->
        if (todos.isEmpty()) {
            EmptyState(
                text = stringResource(R.string.no_tasks),
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(AppTheme.itemSpacing)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoItem(
                        todo = todo,
                        onTodoClick = { viewModel.update(it) },
                        onDeleteClick = { viewModel.delete(it) },
                        onAddToCalendarClick = { viewModel.addToCalendar(it) },
                        modifier = Modifier.padding(horizontal = AppTheme.horizontalPadding)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddTodoDialog(
            title = newTodoTitle,
            onTitleChange = { newTodoTitle = it },
            description = newTodoDescription,
            onDescriptionChange = { newTodoDescription = it },
            selectedDate = selectedDate,
            selectedTime = selectedTime,
            onDateTimeClick = { showDateTimePicker = true },
            onDismiss = { showDialog = false },
            onConfirm = {
                if (newTodoTitle.isNotBlank()) {
                    val reminderTime = selectedDate?.atTime(selectedTime ?: LocalTime.NOON)
                        ?.atZone(ZoneId.systemDefault())
                        ?.toInstant()
                        ?.toEpochMilli()

                    viewModel.insert(
                        TodoModel(
                            title = newTodoTitle,
                            description = newTodoDescription,
                            hasReminder = reminderTime != null,
                            reminderTime = reminderTime
                        )
                    )
                    newTodoTitle = ""
                    newTodoDescription = ""
                    selectedDate = null
                    selectedTime = null
                    showDialog = false
                }
            }
        )
    }

    if (showDateTimePicker) {
        DateTimePickerDialog(
            onDismiss = { showDateTimePicker = false },
            onConfirm = { date, time ->
                selectedDate = date
                selectedTime = time
                showDateTimePicker = false
            }
        )
    }
}