package ru.maxgog.pagebook.ui.calendar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    date: LocalDate,
    allTags: List<Tag>,
    selectedTags: List<Tag>,
    onToggleTag: (Tag) -> Unit,
    onCreateTag: (String, ru.maxgog.pagebook.domain.model.TagColor) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, date: LocalDate, time: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("12:00") }
    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }

    val timePattern = remember { Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") }
    val isTimeValid = remember(time) { timePattern.matches(time) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.add),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.event_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = title.isBlank(),
                    supportingText = {
                        if (title.isBlank()) Text("Пустой текст")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.event_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { if (it.length <= 5) time = it },
                    label = { Text(stringResource(R.string.event_time)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !isTimeValid,
                    supportingText = {
                        if (!isTimeValid) Text("Неправильное время")
                    },
                    placeholder = { Text("HH:MM") }
                )

                // 🆕 Теги
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedTags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                selected = true,
                                onDelete = { onToggleTag(tag) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                TextButton(onClick = { showTagPicker = true }) {
                    Icon(Icons.Default.Label, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(if (selectedTags.isEmpty()) "Добавить теги" else "Изменить теги")
                }

                Text(
                    text = date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (title.isNotBlank() && isTimeValid) {
                        onConfirm(title, description, date, time)
                    }
                },
                enabled = title.isNotBlank() && isTimeValid,
                colors = ButtonDefaults.filledTonalButtonColors()
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )

    if (showTagPicker) {
        TagPickerDialog(
            allTags = allTags,
            selectedTags = selectedTags,
            onToggle = onToggleTag,
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
            onConfirm = { name, color ->
                onCreateTag(name, color)
                showCreateTag = false
            }
        )
    }
}