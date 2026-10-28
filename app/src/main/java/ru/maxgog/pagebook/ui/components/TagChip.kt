package ru.maxgog.pagebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.domain.model.Tag
import androidx.core.graphics.toColorInt

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
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
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