package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Tag

@Composable
fun TagChip(
    tag: Tag,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val color = Color(tag.colorHex.toColorInt())
    val containerColor = if (selected) color else color.copy(alpha = 0.15f)
    val contentColor = if (selected) Color.White else color

    val wrappedModifier = if (onLongClick != null) {
        modifier.pointerInput(tag.id) { detectTapGestures(onLongPress = { onLongClick() }) }
    } else modifier

    FilterChip(
        selected = selected,
        onClick = { onClick?.invoke() },
        label = { Text(tag.name, color = contentColor) },
        leadingIcon = {
            if (tag.emoji != null) {
                Text(text = tag.emoji, fontSize = 16.sp)
            } else {
                Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            }
        },
        trailingIcon = when {
            onEdit != null && onDelete != null -> {
                {
                    Row {
                        EditTrailingIcon(contentColor, onEdit)
                        DeleteTrailingIcon(contentColor, onDelete)
                    }
                }
            }
            onEdit != null   -> { { EditTrailingIcon(contentColor, onEdit) } }
            onDelete != null -> { { DeleteTrailingIcon(contentColor, onDelete) } }
            else             -> null
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor,
            containerColor = containerColor
        ),
        modifier = wrappedModifier
    )
}

@Composable
private fun EditTrailingIcon(contentColor: Color, onClick: () -> Unit) {
    Icon(
        Icons.Default.Edit,
        contentDescription = stringResource(R.string.edit_tag),
        modifier = Modifier
            .size(FilterChipDefaults.IconSize)
            .clickable { onClick() },
        tint = contentColor
    )
}

@Composable
private fun DeleteTrailingIcon(contentColor: Color, onClick: () -> Unit) {
    Icon(
        Icons.Default.Close,
        contentDescription = stringResource(R.string.delete_tag),
        modifier = Modifier
            .size(FilterChipDefaults.IconSize)
            .clickable { onClick() },
        tint = contentColor
    )
}