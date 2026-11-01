package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class ContextAction(
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContextualItem(
    actions: List<ContextAction>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClickEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = modifier.combinedClickable(
                onClick = onClick,
                onLongClick = if (onLongClickEnabled && actions.isNotEmpty()) {
                    { expanded = true }
                } else null
            )
        ) {
            content()
        }

        ContextualMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            actions = actions
        )
    }
}

@Composable
fun ContextualMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    actions: List<ContextAction>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        var dividerInserted = false
        actions.forEachIndexed { index, action ->
            if (action.isDestructive && !dividerInserted && index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                dividerInserted = true
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = action.label,
                        color = if (action.isDestructive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                onClick = {
                    onDismissRequest()
                    action.onClick()
                },
                leadingIcon = {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = if (action.isDestructive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            )
        }
    }
}