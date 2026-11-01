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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.model.TagUsage
import ru.maxgog.pagebook.domain.usecase.tag.GetTagUsageUseCase
import javax.inject.Inject

@HiltViewModel
class TagEditViewModel @Inject constructor(
    private val getTagUsage: GetTagUsageUseCase
) : ViewModel() {
    private val _usage = MutableStateFlow<TagUsage?>(null)
    val usage: StateFlow<TagUsage?> = _usage.asStateFlow()

    fun load(tagId: Int) {
        viewModelScope.launch { _usage.value = getTagUsage(tagId) }
    }
}

@Composable
fun CreateTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: TagColor, emoji: String?) -> Unit,
    initialTag: Tag? = null,
    showUsage: Boolean = initialTag != null,
    viewModel: TagEditViewModel = hiltViewModel()
) {
    val isEdit = initialTag != null
    val usage by viewModel.usage.collectAsStateWithLifecycle()

    LaunchedEffect(initialTag?.id) {
        if (initialTag != null) viewModel.load(initialTag.id)
    }

    var name by remember(initialTag) { mutableStateOf(initialTag?.name ?: "") }
    var selectedColor by remember(initialTag) {
        mutableStateOf(
            initialTag?.let { t -> TagColor.entries.firstOrNull { it.hex == t.colorHex } }
                ?: TagColor.BLUE
        )
    }
    var selectedEmoji by remember(initialTag) { mutableStateOf(initialTag?.emoji) }
    var showEmojiPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (isEdit) R.string.edit_tag else R.string.new_tag))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(selectedColor.hex.toColorInt()).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedEmoji != null) Text(selectedEmoji!!, fontSize = 22.sp)
                        else Box(
                            Modifier.size(14.dp).clip(CircleShape)
                                .background(Color(selectedColor.hex.toColorInt()))
                        )
                    }
                    Text(
                        text = name.ifBlank { stringResource(R.string.tag_name_hint) },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (name.isBlank())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.tag_name)) },
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
                        if (selectedEmoji != null) stringResource(R.string.emoji_prefix, selectedEmoji!!)
                        else stringResource(R.string.add_emoji)
                    )
                }

                if (showUsage && usage != null) {
                    val u = usage!!
                    Text(
                        text = stringResource(R.string.tag_usage_format, u.notes, u.tasks, u.events),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(stringResource(R.string.color), style = MaterialTheme.typography.titleSmall)
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
            ) {
                Text(stringResource(if (isEdit) R.string.save else R.string.create))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )

    if (showEmojiPicker) {
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji -> selectedEmoji = emoji; showEmojiPicker = false }
        )
    }
}