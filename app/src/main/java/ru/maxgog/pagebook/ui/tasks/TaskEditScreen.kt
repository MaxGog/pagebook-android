package ru.maxgog.pagebook.ui.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.DateTimePickerDialog
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    taskId: Int? = null,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()

    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }

    val currentTask = (uiState as? TaskEditUiState.Editing)?.task
    val currentTags = when (val s = uiState) {
        is TaskEditUiState.Editing -> s.tags
        is TaskEditUiState.NewTask -> s.selectedTags
        else -> emptyList()
    }
    val isLoading = uiState is TaskEditUiState.Loading
    val isNewTask = uiState is TaskEditUiState.NewTask

    var title by remember(currentTask?.id) { mutableStateOf(currentTask?.title ?: "") }
    var description by remember(currentTask?.id) { mutableStateOf(currentTask?.description ?: "") }
    var reminderTime by remember(currentTask?.id) { mutableStateOf(currentTask?.reminderTime) }

    LaunchedEffect(taskId) {
        if (taskId != null) viewModel.setTaskId(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TaskEditEvent.Saved, TaskEditEvent.Deleted -> onBack()
            }
        }
    }

    val saveEnabled = title.isNotBlank() && !isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(if (isNewTask) R.string.new_task else R.string.edit_task)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showTagPicker = true }) {
                        Icon(Icons.Default.Label, contentDescription = stringResource(R.string.tags))
                    }
                    FilledTonalIconButton(
                        onClick = { viewModel.save(title, description, reminderTime) },
                        enabled = saveEnabled
                    ) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
                    }
                    if (currentTask != null) {
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            uiState is TaskEditUiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) { Text((uiState as TaskEditUiState.Error).message) }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    maxLines = 6,
                    shape = MaterialTheme.shapes.large
                )

                if (currentTags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentTags.forEach { tag ->
                            TagChip(tag = tag, selected = true, onDelete = { viewModel.toggleTag(tag) })
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.reminder), style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = reminderTime?.let {
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(it))
                                } ?: stringResource(R.string.reminder_not_set),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (reminderTime != null) {
                            TextButton(onClick = { reminderTime = null }) {
                                Text(stringResource(R.string.clear))
                            }
                        }
                    }
                }

                FilledTonalButton(
                    onClick = { showDateTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        stringResource(
                            if (reminderTime == null) R.string.set_reminder else R.string.change_reminder
                        )
                    )
                }

                if (currentTask?.noteId != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            stringResource(R.string.linked_to_note, currentTask.noteId!!),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialMillis = reminderTime,
            onDismiss = { showDateTimePicker = false },
            onConfirm = { millis ->
                reminderTime = millis
                showDateTimePicker = false
            }
        )
    }

    if (showTagPicker) {
        TagPickerDialog(
            allTags = allTags,
            selectedTags = currentTags,
            onToggle = viewModel::toggleTag,
            onCreateTag = {
                showTagPicker = false
                showCreateTag = true
            },
            onDismiss = { showTagPicker = false }
        )
    }

    if (showCreateTag) {
        CreateTagDialog(
            onDismiss = { showCreateTag = false },
            onConfirm = { name, color, emoji ->
                viewModel.createAndToggleTag(name, color, emoji)
                showCreateTag = false
            }
        )
    }
}