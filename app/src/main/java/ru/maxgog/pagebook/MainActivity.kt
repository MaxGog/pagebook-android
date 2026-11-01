package ru.maxgog.pagebook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.maxgog.pagebook.core.notification.AppNotificationType
import ru.maxgog.pagebook.ui.app.MainViewModel
import ru.maxgog.pagebook.ui.navigation.CombinedApp
import ru.maxgog.pagebook.ui.notification.AppNotificationHost
import ru.maxgog.pagebook.ui.notification.FursonaSnackbar
import ru.maxgog.pagebook.ui.notification.FursonaSnackbarVisuals
import ru.maxgog.pagebook.ui.theme.AppTheme
import ru.maxgog.pagebook.ui.theme.CombinedAppTheme
import ru.maxgog.pagebook.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            val mainViewModel: MainViewModel = hiltViewModel()

            val darkTheme = when (themeMode) {
                AppTheme.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppTheme.ThemeMode.LIGHT -> false
                AppTheme.ThemeMode.DARK -> true
            }

            CombinedAppTheme(darkTheme = darkTheme) {
                val snackbarHostState = remember { SnackbarHostState() }
                val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                val navigationBarHeight = if (isCompact) 80.dp else 0.dp

                AppNotificationHost(
                    manager = mainViewModel.notificationManager,
                    snackbarHostState = snackbarHostState
                )

                Box(Modifier.fillMaxSize()) {
                    CombinedApp(
                        windowWidthSizeClass = windowSizeClass.widthSizeClass
                    )

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .imePadding()
                            .navigationBarsPadding()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = navigationBarHeight + 8.dp
                            )
                    ) { data ->
                        val visuals = data.visuals
                        FursonaSnackbar(
                            message = visuals.message,
                            type = (visuals as? FursonaSnackbarVisuals)?.type
                                ?: AppNotificationType.INFO,
                            withDismissAction = visuals.withDismissAction,
                            onDismiss = { data.dismiss() }
                        )
                    }
                }
            }
        }
    }
}