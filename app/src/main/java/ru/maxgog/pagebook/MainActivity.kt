package ru.maxgog.pagebook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.maxgog.pagebook.ui.navigation.CombinedApp
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

            val darkTheme = when (themeMode) {
                AppTheme.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppTheme.ThemeMode.LIGHT -> false
                AppTheme.ThemeMode.DARK -> true
            }

            CombinedAppTheme(darkTheme = darkTheme) {
                CombinedApp(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass
                )
            }
        }
    }
}