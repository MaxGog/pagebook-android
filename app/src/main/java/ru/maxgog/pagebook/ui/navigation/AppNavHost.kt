package ru.maxgog.pagebook.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.maxgog.pagebook.ui.calendar.CalendarScreen
import ru.maxgog.pagebook.ui.notes.NotesScreen
import ru.maxgog.pagebook.ui.notes.NoteEditScreen
import ru.maxgog.pagebook.ui.settings.SettingsScreen
import ru.maxgog.pagebook.ui.tasks.TaskEditScreen
import ru.maxgog.pagebook.ui.tasks.TaskListDetailPane
import ru.maxgog.pagebook.ui.tasks.TaskListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TaskListDestination,
        modifier = modifier
    ) {
        composable<TaskListDestination> {
            val openSettings = { navController.navigate(SettingsDestination) }
            if (windowWidthSizeClass == WindowWidthSizeClass.Expanded) {
                TaskListDetailPane(
                    navController = navController,
                    onSettingsClick = openSettings
                )
            } else {
                TaskListScreen(
                    onAddTask = { navController.navigate(TaskEditDestination()) },
                    onTaskClick = { id -> navController.navigate(TaskEditDestination(id)) },
                    onSettingsClick = openSettings
                )
            }
        }

        composable<TaskEditDestination> {
            TaskEditScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(TaskListDestination) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<CalendarDestination> {
            CalendarScreen(
                onSettingsClick = { navController.navigate(SettingsDestination) }
            )
        }

        composable<NotesDestination> {
            NotesScreen(
                onNoteClick = { id -> navController.navigate(NoteDetailDestination(id)) },
                onAddNote = { navController.navigate(NoteAddDestination) },
                onSettingsClick = { navController.navigate(SettingsDestination) }
            )
        }

        composable<NoteAddDestination> {
            NoteEditScreen(onBack = { navController.popBackStack() })
        }

        composable<NoteDetailDestination> {
            NoteEditScreen(onBack = { navController.popBackStack() })
        }

        composable<SettingsDestination> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}