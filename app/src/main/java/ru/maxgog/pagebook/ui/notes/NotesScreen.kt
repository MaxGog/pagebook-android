package ru.maxgog.pagebook.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.ui.EmptyState
import ru.maxgog.pagebook.ui.components.OverflowMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (Int) -> Unit,
    onAddNote: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.notes)) },
                actions = {
                    OverflowMenu(onSettingsClick = onSettingsClick)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNote,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add)) }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is NotesUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is NotesUiState.Error -> EmptyState(
                text = state.message,
                modifier = Modifier.padding(innerPadding)
            )

            is NotesUiState.Success -> {
                if (state.notes.isEmpty()) {
                    EmptyState(
                        text = stringResource(R.string.no_notes),
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding,
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            NoteItem(note = note, onClick = { onNoteClick(note.id) })
                        }
                    }
                }
            }
        }
    }
}