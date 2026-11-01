package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.FilterMode
import ru.maxgog.pagebook.domain.model.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagFilterRow(
    allTags: List<Tag>,
    selectedTagIds: Set<Int>,
    mode: FilterMode,
    onToggleTag: (Int) -> Unit,
    onToggleMode: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (allTags.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item(key = "mode") {
            AssistChip(
                onClick = onToggleMode,
                label = { Text(if (mode == FilterMode.OR) "OR" else "AND") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = stringResource(
                            if (mode == FilterMode.OR) R.string.filter_mode_or_desc
                            else R.string.filter_mode_and_desc
                        )
                    )
                }
            )
        }
        items(allTags, key = { it.id }) { tag ->
            TagChip(
                tag = tag,
                selected = tag.id in selectedTagIds,
                onClick = { onToggleTag(tag.id) },
                onLongClick = onToggleMode
            )
        }
        if (selectedTagIds.isNotEmpty()) {
            item(key = "clear") {
                AssistChip(
                    onClick = onClearFilters,
                    label = { Text(stringResource(R.string.clear_filters)) },
                    leadingIcon = { Icon(Icons.Default.Close, contentDescription = null) }
                )
            }
        }
    }
}