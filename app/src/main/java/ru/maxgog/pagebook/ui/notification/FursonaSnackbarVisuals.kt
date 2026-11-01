package ru.maxgog.pagebook.ui.notification

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import ru.maxgog.pagebook.core.notification.AppNotificationType

data class FursonaSnackbarVisuals(
    override val message: String,
    val type: AppNotificationType,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = true,
    override val duration: SnackbarDuration = type.toSnackbarDuration()
) : SnackbarVisuals