package ru.maxgog.pagebook.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEventDialog(
    date: LocalDate,
    allTags: List<Tag>,
    selectedTags: List<Tag>,
    onToggleTag: (Tag) -> Unit,
    onCreateTag: (name: String, color: TagColor, emoji: String?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, date: LocalDate, time: String) -> Unit,
    initialEvent: Event? = null,
    modifier: Modifier = Modifier
) {
    val isEditing = initialEvent != null

    var title by remember(initialEvent?.id) {
        mutableStateOf(initialEvent?.title ?: "")
    }
    var description by remember(initialEvent?.id) {
        mutableStateOf(initialEvent?.description ?: "")
    }
    var time by remember(initialEvent?.id) {
        mutableStateOf(initialEvent?.time ?: "12:00")
    }
    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }

    val timePattern = remember { Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") }
    val isTimeValid = remember(time) { timePattern.matches(time) }
    val canConfirm = title.isNotBlank() && isTimeValid

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        modifier = modifier,
        title = {
            Text(
                text = stringResource(if (isEditing) R.string.edit_event else R.string.add),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.event_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = title.isBlank(),
                    supportingText = {
                        if (title.isBlank()) {
                            Text(stringResource(R.string.title_required))
                        }
                    },
                    shape = MaterialTheme.shapes.large
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.event_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = MaterialTheme.shapes.large
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { if (it.length <= 5) time = it },
                    label = { Text(stringResource(R.string.event_time)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !isTimeValid,
                    supportingText = {
                        if (!isTimeValid) {
                            Text(stringResource(R.string.invalid_time))
                        }
                    },
                    placeholder = { Text("HH:MM") },
                    shape = MaterialTheme.shapes.large
                )

                if (!isEditing) {
                    if (selectedTags.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectedTags.forEach { tag ->
                                TagChip(
                                    tag = tag,
                                    selected = true,
                                    onDelete = { onToggleTag(tag) }
                                )
                            }
                        }
                    }
                    AssistChip(
                        onClick = { showTagPicker = true },
                        label = {
                            Text(stringResource(R.string.tags))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    )
                }

                Text(
                    text = date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (canConfirm) {
                        onConfirm(title, description, date, time)
                    }
                },
                enabled = canConfirm,
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = stringResource(if (isEditing) R.string.save else R.string.add),
                    fontWeight = FontWeight.SemiBold
                )
            }
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
            onConfirm = { name, color, emoji ->
                onCreateTag(name, color, emoji)
                showCreateTag = false
            }
        )
    }
}