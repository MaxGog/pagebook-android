package ru.maxgog.pagebook.core.notification

import androidx.compose.material3.SnackbarDuration

enum class AppNotificationType {
    SUCCESS,
    ERROR,
    INFO,
    EASTER_EGG;

    fun toSnackbarDuration(): SnackbarDuration = when (this) {
        SUCCESS, INFO -> SnackbarDuration.Short
        ERROR -> SnackbarDuration.Long
        EASTER_EGG -> SnackbarDuration.Long
    }
}