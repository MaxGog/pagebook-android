package ru.maxgog.pagebook.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun CombinedApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberNavigationSuiteScaffoldState()

    val currentDestination = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination

    val showNavigation = currentDestination?.let { dest ->
        dest.hasRoute<TaskListDestination>() ||
                dest.hasRoute<NotesDestination>() ||
                dest.hasRoute<CalendarDestination>()
    } ?: false

    LaunchedEffect(showNavigation) {
        if (showNavigation) scaffoldState.show() else scaffoldState.hide()
    }

    NavigationSuiteScaffold(
        state = scaffoldState,
        navigationSuiteItems = {
            BottomNavBar(
                currentDestination = currentDestination,
                navController = navController
            )
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                windowWidthSizeClass = windowWidthSizeClass,
                modifier = Modifier.fillMaxSize()
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}