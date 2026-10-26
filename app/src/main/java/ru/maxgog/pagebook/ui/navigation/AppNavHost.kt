package ru.maxgog.pagebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.maxgog.pagebook.ui.screens.CalendarScreen
import ru.maxgog.pagebook.ui.screens.SettingsScreen
import ru.maxgog.pagebook.ui.screens.TodoScreen
import ru.maxgog.pagebook.ui.screens.note_edit.NoteEditScreen
import ru.maxgog.pagebook.ui.screens.notes.NotesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TodoDestination,
        modifier = modifier
    ) {
        composable<TodoDestination> { TodoScreen() }
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
    }
}