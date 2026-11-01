package ru.maxgog.pagebook.ui.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.maxgog.pagebook.core.notification.AppNotificationManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val notificationManager: AppNotificationManager
) : ViewModel()