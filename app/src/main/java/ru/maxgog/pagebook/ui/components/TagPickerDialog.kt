package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Tag

@Composable
fun TagPickerDialog(
    allTags: List<Tag>,
    selectedTags: List<Tag>,
    onToggle: (Tag) -> Unit,
    onCreateTag: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tags)) },
        text = {
            if (allTags.isEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.no_tags_yet))
                    TextButton(onClick = onCreateTag) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.create_tag))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allTags, key = { it.id }) { tag ->
                        val isSelected = selectedTags.any { it.id == tag.id }
                        TagChip(
                            tag = tag,
                            selected = isSelected,
                            onClick = { onToggle(tag) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        TextButton(onClick = onCreateTag) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.create_tag))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.done)) }
        }
    )
}