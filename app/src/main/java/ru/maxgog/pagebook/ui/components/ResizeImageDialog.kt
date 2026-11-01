package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResizeImageDialog(
    initialWidthSp: Float,
    initialHeightSp: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float) -> Unit
) {
    var width by remember { mutableFloatStateOf(initialWidthSp) }
    var height by remember { mutableFloatStateOf(initialHeightSp) }
    val ratio = if (initialWidthSp > 0f) initialHeightSp / initialWidthSp else 1f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Размер изображения") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Ширина: ${width.toInt()} sp")
                Slider(
                    value = width,
                    onValueChange = {
                        width = it
                        height = it * ratio
                    },
                    valueRange = 50f..500f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(width, height) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}