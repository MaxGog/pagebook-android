package ru.maxgog.pagebook.ui.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    modifier: Modifier = Modifier,
    withDismissAction: Boolean = true,
    onDismiss: () -> Unit = {}
) {
    val containerColor = type.containerColor()
    val contentColor = type.contentColor()
    val accentColor = type.accentColor()

    // Используем Box, чтобы картинка могла выходить за пределы Surface
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // --- ФОН СНЕКБАРА ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                // Отступаем слева, чтобы освободить место для фурсоны
                .padding(start = 80.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = containerColor,
            contentColor = contentColor,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp,
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
        ) {
            Row(
                // Внутренний отступ текста, чтобы он не наезжал на картинку
                modifier = Modifier.padding(start = 72.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (withDismissAction) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.notification_dismiss),
                            tint = contentColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // --- ФУРСОНА (выглядывает слева снизу) ---
        Image(
            painter = painterResource(R.drawable.ic_fursona_avatar),
            contentDescription = stringResource(R.string.fursona_avatar_content_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                // Сдвигаем вниз (y = 8.dp), чтобы она перекрывала нижнюю границу
                .offset(x = 0.dp, y = 8.dp)
                // Размер должен быть больше высоты снекбара
                .size(130.dp),
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