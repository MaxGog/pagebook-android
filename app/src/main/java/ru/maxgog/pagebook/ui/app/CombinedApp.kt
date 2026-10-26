package ru.maxgog.pagebook.ui.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.maxgog.pagebook.ui.navigation.AppNavHost
import ru.maxgog.pagebook.ui.navigation.BottomNavBar
import ru.maxgog.pagebook.ui.navigation.CalendarDestination
import ru.maxgog.pagebook.ui.navigation.NotesDestination
import ru.maxgog.pagebook.ui.navigation.TodoDestination

@Composable
fun CombinedApp(
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val currentDestination = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination

    val showBottomBar = currentDestination?.let { destination ->
        destination.hasRoute<TodoDestination>() ||
                destination.hasRoute<NotesDestination>() ||
                destination.hasRoute<CalendarDestination>()
    } ?: false

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}