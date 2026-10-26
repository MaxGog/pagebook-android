package ru.maxgog.pagebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.maxgog.pagebook.ui.navigation.CombinedApp
import ru.maxgog.pagebook.ui.theme.AppTheme
import ru.maxgog.pagebook.ui.theme.CombinedAppTheme
import ru.maxgog.pagebook.ui.theme.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            val darkTheme = when (themeMode) {
                AppTheme.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppTheme.ThemeMode.LIGHT -> false
                AppTheme.ThemeMode.DARK -> true
            }

            CombinedAppTheme(darkTheme = darkTheme) {
                CombinedApp()
            }
        }
    }
}