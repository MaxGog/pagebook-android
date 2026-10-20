package ru.maxgog.pagebook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.ui.EmptyState
import ru.maxgog.pagebook.ui.NotesDestinations
import ru.maxgog.pagebook.ui.items.NoteItem
import ru.maxgog.pagebook.ui.theme.AppTheme
import ru.maxgog.pagebook.ui.theme.SetTransparentSystemBars
import ru.maxgog.pagebook.viewmodels.NotesViewModel

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    NavHost(
        navController = navController,
        startDestination = NotesDestinations.LIST_ROUTE,
        route = NotesDestinations.ROOT_ROUTE
    ) {
        composable(NotesDestinations.LIST_ROUTE) {
            NoteListScreen(
                notes = notes,
                onNoteClick = { id -> navController.navigate("${NotesDestinations.DETAIL_ROUTE}/$id") },
                onAddNote = { navController.navigate(NotesDestinations.ADD_ROUTE) },
                onSettingsClick = { navController.navigate(NotesDestinations.SETTINGS_ROUTE) }
            )
        }

        composable(NotesDestinations.ADD_ROUTE) {
            NoteEditScreen(
                note = null,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("${NotesDestinations.DETAIL_ROUTE}/{id}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val note = notes.firstOrNull { note -> note.id == noteId }
            NoteEditScreen(
                note = note,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NotesDestinations.SETTINGS_ROUTE) {
            SettingsScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<NoteModel>,
    onNoteClick: (Int) -> Unit,
    onAddNote: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SetTransparentSystemBars()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.notes)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        },
    ) { innerPadding ->
        if (notes.isEmpty()) {
            EmptyState(
                text = stringResource(R.string.no_notes),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(AppTheme.itemSpacing)
            ) {
                items(notes, key = { note -> note.id }) { note ->
                    NoteItem(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        modifier = Modifier.padding(horizontal = AppTheme.horizontalPadding)
                    )
                }
            }
        }
    }
}