package ru.maxgog.pagebook.core.notification

import androidx.annotation.StringRes

sealed interface AppNotification {

    val type: AppNotificationType

    data class FromRes(
        @StringRes val messageRes: Int,
        override val type: AppNotificationType = AppNotificationType.INFO,
        val formatArgs: List<Any> = emptyList()
    ) : AppNotification

    data class FromText(
        val message: String,
        override val type: AppNotificationType = AppNotificationType.INFO
    ) : AppNotification
}