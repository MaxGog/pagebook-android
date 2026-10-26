package ru.maxgog.pagebook.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import ru.maxgog.pagebook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, date: LocalDate, time: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("12:00") }
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
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.event_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = title.isBlank(),
                    supportingText = {
                        if (title.isBlank()) {
                            Text("Пустой текст")
                        }
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
                        if (!isTimeValid) {
                            Text("Неправильное время")
                        }
                    },
                    placeholder = { Text("HH:MM") }
                )

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
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}