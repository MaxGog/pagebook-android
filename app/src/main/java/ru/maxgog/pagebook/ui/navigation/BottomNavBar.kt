package ru.maxgog.pagebook.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.maxgog.pagebook.R

@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar {
        val items = listOf(
            Triple(TaskListDestination, Icons.Default.Checklist, R.string.todo_list),
            Triple(NotesDestination, Icons.AutoMirrored.Filled.Note, R.string.notes),
            Triple(CalendarDestination, Icons.Default.CalendarToday, R.string.calendar)
        )

        items.forEach { (destination, icon, labelResId) ->
            val selected = currentDestination?.hasRoute(destination::class) == true
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(stringResource(labelResId)) },
                selected = selected,
                onClick = {
                    navController.navigate(destination) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}