package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.maxgog.pagebook.R

@Composable
fun EmojiPickerDialog(
    onDismiss: () -> Unit,
    onEmojiSelected: (String?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(EmojiCatalog.categories.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.choose_emoji)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EmojiCatalog.categories.size) { index ->
                        val category = EmojiCatalog.categories[index]
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category },
                            label = { Text(stringResource(category.nameRes)) }
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedCategory.emojis) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable { onEmojiSelected(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 26.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEmojiSelected(null) }) {
                Text(stringResource(R.string.without_emoji))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}