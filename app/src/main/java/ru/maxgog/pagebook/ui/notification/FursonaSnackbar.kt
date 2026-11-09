package ru.maxgog.pagebook.ui.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.core.notification.AppNotificationType

@Composable
fun FursonaSnackbar(
    message: String,
    type: AppNotificationType,
    modifier: Modifier = Modifier
) {
    val containerColor = type.containerColor()
    val contentColor = type.contentColor()
    val accentColor = type.accentColor()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = containerColor,
            contentColor = contentColor,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp,
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(start = 120.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                        .offset(x = 12.dp)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_fursona_avatar),
            contentDescription = stringResource(R.string.fursona_avatar_content_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 8.dp, y = (-56).dp)
                .size(160.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun AppNotificationType.containerColor(): Color = when (this) {
    AppNotificationType.SUCCESS -> MaterialTheme.colorScheme.secondaryContainer
    AppNotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
    AppNotificationType.INFO -> MaterialTheme.colorScheme.surfaceContainerHigh
    AppNotificationType.EASTER_EGG -> MaterialTheme.colorScheme.tertiaryContainer
}

@Composable
private fun AppNotificationType.contentColor(): Color = when (this) {
    AppNotificationType.SUCCESS -> MaterialTheme.colorScheme.onSecondaryContainer
    AppNotificationType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
    AppNotificationType.INFO -> MaterialTheme.colorScheme.onSurface
    AppNotificationType.EASTER_EGG -> MaterialTheme.colorScheme.onTertiaryContainer
}

@Composable
private fun AppNotificationType.accentColor(): Color = when (this) {
    AppNotificationType.SUCCESS -> MaterialTheme.colorScheme.secondary
    AppNotificationType.ERROR -> MaterialTheme.colorScheme.error
    AppNotificationType.INFO -> MaterialTheme.colorScheme.primary
    AppNotificationType.EASTER_EGG -> MaterialTheme.colorScheme.tertiary
}