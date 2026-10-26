package ru.maxgog.pagebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.maxgog.pagebook.ui.calendar.CalendarScreen
import ru.maxgog.pagebook.ui.settings.SettingsScreen
import ru.maxgog.pagebook.ui.notes.note_edit.NoteEditScreen
import ru.maxgog.pagebook.ui.notes.NotesScreen
import ru.maxgog.pagebook.ui.tasks.TaskEditScreen
import ru.maxgog.pagebook.ui.tasks.TaskListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TaskListDestination,
        modifier = modifier
    ) {
        composable<TaskListDestination> {
            TaskListScreen(
                onAddTask = { navController.navigate(TaskEditDestination()) }
            )
        }

        composable<CalendarDestination> { CalendarScreen() }

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

        composable<TaskEditDestination> {
            TaskEditScreen(onBack = { navController.popBackStack() })
        }
    }
}