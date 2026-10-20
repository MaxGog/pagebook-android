package ru.maxgog.pagebook.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.maxgog.pagebook.ui.screens.CalendarScreen
import ru.maxgog.pagebook.ui.screens.NoteEditScreen
import ru.maxgog.pagebook.ui.screens.NotesScreen
import ru.maxgog.pagebook.ui.screens.SettingsScreen
import ru.maxgog.pagebook.ui.screens.TodoScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Todo.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(Screen.Todo.route) { TodoScreen() }
        composable(Screen.Calendar.route) { CalendarScreen() }

        composable(Screen.Notes.route) {
            NotesScreen(
                onNoteClick = { id -> navController.navigate(Routes.noteDetail(id)) },
                onAddNote = { navController.navigate(Routes.NOTE_ADD) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.NOTE_ADD) {
            NoteEditScreen(
                noteId = null,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.NOTE_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_NOTE_ID) { type = NavType.IntType })
        ) { entry ->
            NoteEditScreen(
                noteId = entry.arguments?.getInt(Routes.ARG_NOTE_ID),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}