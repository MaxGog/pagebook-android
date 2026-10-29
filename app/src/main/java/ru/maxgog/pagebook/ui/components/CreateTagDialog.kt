package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.maxgog.pagebook.domain.model.TagColor
import androidx.core.graphics.toColorInt

@Composable
fun CreateTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: TagColor, emoji: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(TagColor.BLUE) }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }
    var showEmojiPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый тег") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Превью
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(selectedColor.hex)).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedEmoji != null) {
                            Text(selectedEmoji!!, fontSize = 22.sp)
                        } else {
                            Box(
                                Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(selectedColor.hex.toColorInt()))
                            )
                        }
                    }
                    Text(
                        text = name.ifBlank { "Название тега" },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (name.isBlank())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = { showEmojiPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.EmojiEmotions, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (selectedEmoji != null) "Эмодзи: $selectedEmoji"
                        else "Добавить эмодзи"
                    )
                }
                
                Text("Цвет", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TagColor.entries.forEach { color ->
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(color.hex.toColorInt()))
                                .border(
                                    width = if (color == selectedColor) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, selectedColor, selectedEmoji) },
                enabled = name.isNotBlank()
            ) { Text("Создать") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )

    if (showEmojiPicker) {
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji ->
                selectedEmoji = emoji
                showEmojiPicker = false
            }
        )
    }
}