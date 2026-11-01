package ru.maxgog.pagebook.ui.notification

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import ru.maxgog.pagebook.core.notification.AppNotification
import ru.maxgog.pagebook.core.notification.AppNotificationManager

@Composable
fun AppNotificationHost(
    manager: AppNotificationManager,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(manager, snackbarHostState) {
        manager.events.collect { notification ->
            val message = when (notification) {
                is AppNotification.FromRes ->
                    context.getString(
                        notification.messageRes,
                        *notification.formatArgs.toTypedArray()
                    )
                is AppNotification.FromText -> notification.message
            }

            snackbarHostState.currentSnackbarData?.dismiss()

            snackbarHostState.showSnackbar(
                FursonaSnackbarVisuals(
                    message = message,
                    type = notification.type
                )
            )
        }
    }
}