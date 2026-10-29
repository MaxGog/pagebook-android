package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import ru.maxgog.pagebook.domain.model.Tag

@Composable
fun TagChip(
    tag: Tag,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val color = Color(tag.colorHex.toColorInt())
    val containerColor = if (selected) color else color.copy(alpha = 0.15f)
    val contentColor = if (selected) Color.White else color

    FilterChip(
        selected = selected,
        onClick = { onClick?.invoke() },
        label = { Text(tag.name, color = contentColor) },
        leadingIcon = {
            if (tag.emoji != null) {
                Text(text = tag.emoji, fontSize = 16.sp)
            } else {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        },
        trailingIcon = if (onDelete != null) {
            {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Удалить тег",
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                        .clickable { onDelete() },
                    tint = contentColor
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor,
            containerColor = containerColor
        ),
        modifier = modifier
    )
}