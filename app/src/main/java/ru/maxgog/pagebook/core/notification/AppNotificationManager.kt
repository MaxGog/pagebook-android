package ru.maxgog.pagebook.core.notification

import androidx.annotation.StringRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor() {

    private val _events = Channel<AppNotification>(Channel.BUFFERED)
    val events: Flow<AppNotification> = _events.receiveAsFlow()

    fun show(notification: AppNotification) {
        _events.trySend(notification)
    }

    fun success(@StringRes messageRes: Int, vararg formatArgs: Any) {
        show(AppNotification.FromRes(messageRes, AppNotificationType.SUCCESS, formatArgs.toList()))
    }

    fun error(@StringRes messageRes: Int, vararg formatArgs: Any) {
        show(AppNotification.FromRes(messageRes, AppNotificationType.ERROR, formatArgs.toList()))
    }

    fun info(@StringRes messageRes: Int, vararg formatArgs: Any) {
        show(AppNotification.FromRes(messageRes, AppNotificationType.INFO, formatArgs.toList()))
    }

    fun easterEgg(@StringRes messageRes: Int, vararg formatArgs: Any) {
        show(AppNotification.FromRes(messageRes, AppNotificationType.EASTER_EGG, formatArgs.toList()))
    }

    fun successText(message: String) =
        show(AppNotification.FromText(message, AppNotificationType.SUCCESS))

    fun errorText(message: String) =
        show(AppNotification.FromText(message, AppNotificationType.ERROR))

    fun infoText(message: String) =
        show(AppNotification.FromText(message, AppNotificationType.INFO))
}