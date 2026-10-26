package ru.maxgog.pagebook.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentTask = (uiState as? TaskEditUiState.Editing)?.task
    val isLoading = uiState is TaskEditUiState.Loading
    val isNewTask = uiState is TaskEditUiState.NewTask

    var title by remember(currentTask?.id) { mutableStateOf(currentTask?.title ?: "") }
    var description by remember(currentTask?.id) { mutableStateOf(currentTask?.description ?: "") }
    var reminderTime by remember(currentTask?.id) { mutableStateOf(currentTask?.reminderTime) }

    val saveEnabled = title.isNotBlank() && !isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewTask) "Новая задача" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.save(title, description, reminderTime)
                            onBack()
                        },
                        enabled = saveEnabled
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                    if (currentTask != null) {
                        IconButton(onClick = {
                            viewModel.delete()
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 6
                )

                // Напоминание
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Напоминание",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = reminderTime?.let {
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                                        .format(Date(it))
                                } ?: "Не задано",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (reminderTime != null) {
                            TextButton(onClick = { reminderTime = null }) {
                                Text("Убрать")
                            }
                        }
                    }
                }

                // TODO: заменить на ваш DateTimePickerDialog
                Button(
                    onClick = {
                        // Временная заглушка: напоминание через 1 час от текущего момента
                        reminderTime = System.currentTimeMillis() + 60 * 60 * 1000
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (reminderTime == null) "Установить напоминание" else "Изменить напоминание")
                }

                if (currentTask?.noteId != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            "Связана с заметкой #${currentTask.noteId}",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}