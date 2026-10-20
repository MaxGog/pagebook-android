package ru.maxgog.pagebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import ru.maxgog.pagebook.ui.app.CombinedApp
import ru.maxgog.pagebook.ui.theme.PageBookTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PageBookTheme {
                CompositionLocalProvider(LocalViewModelStoreOwner provides this) {
                    CombinedApp()
                }
            }
        }
    }
}