package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialMillis: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val zone = ZoneId.systemDefault()
    val initial = initialMillis?.let {
        Instant.ofEpochMilli(it).atZone(zone).toLocalDateTime()
    } ?: LocalDateTime.now().plusHours(1)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.toLocalDate()
            .atStartOfDay(zone).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text(stringResource(R.string.reminder), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DatePicker(state = datePickerState, title = null, headline = null, showModeToggle = false)
                HorizontalDivider()
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    val dateMillis = datePickerState.selectedDateMillis ?: return@FilledTonalButton
                    val date = Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate()
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onConfirm(date.atTime(time).atZone(zone).toInstant().toEpochMilli())
                },
                shape = MaterialTheme.shapes.large
            ) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = MaterialTheme.shapes.large) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}