package ru.maxgog.pagebook.ui.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.ui.app.EmptyState
import ru.maxgog.pagebook.ui.components.ContextAction
import ru.maxgog.pagebook.ui.components.ContextualItem
import ru.maxgog.pagebook.ui.components.FilterBadgeIcon
import ru.maxgog.pagebook.ui.components.OverflowMenu
import ru.maxgog.pagebook.ui.components.TagFilterRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (Int) -> Unit,
    onAddNote: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var filterRowVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(filterState.count) {
        if (filterState.count > 0) filterRowVisible = true
    }
    
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    FilterBadgeIcon(
                        count = filterState.count,
                        onClick = { filterRowVisible = !filterRowVisible }
                    )
                    OverflowMenu(onSettingsClick = onSettingsClick)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        text = stringResource(R.string.add),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = filterRowVisible && allTags.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                TagFilterRow(
                    allTags = allTags,
                    selectedTagIds = filterState.tagIds,
                    mode = filterState.mode,
                    onToggleTag = viewModel::toggleTagFilter,
                    onToggleMode = viewModel::toggleFilterMode,
                    onClearFilters = viewModel::clearTagFilter,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Box(Modifier.weight(1f)) {
                when (val state = uiState) {
                    is NotesUiState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is NotesUiState.Error -> EmptyState(
                        text = state.message,
                        modifier = Modifier.fillMaxSize()
                    )

                    is NotesUiState.Success -> {
                        if (state.notes.isEmpty()) {
                            EmptyState(
                                text = if (filterState.isEmpty)
                                    stringResource(R.string.no_notes)
                                else
                                    stringResource(R.string.no_matching_items),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 4.dp,
                                    bottom = 100.dp
                                ),
                                verticalItemSpacing = 12.dp,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.notes, key = { it.id }) { note ->
                                    val actions = rememberNoteActions(
                                        note = note,
                                        onEdit = { onNoteClick(note.id) },
                                        onDelete = { noteToDelete = note }
                                    )
                                    ContextualItem(
                                        actions = actions,
                                        onClick = { onNoteClick(note.id) }
                                    ) {
                                        NoteItem(note = note)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_note_confirm, note.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(note)
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun rememberNoteActions(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
): List<ContextAction> {
    val editLabel = stringResource(R.string.edit_note)
    val tagsLabel = stringResource(R.string.tags)
    val deleteLabel = stringResource(R.string.delete)
    return remember(note, editLabel, tagsLabel, deleteLabel) {
        listOf(
            ContextAction(
                label = editLabel,
                icon = Icons.Default.Edit,
                onClick = onEdit
            ),
            ContextAction(
                label = tagsLabel,
                icon = Icons.Default.Label,
                onClick = onEdit
            ),
            ContextAction(
                label = deleteLabel,
                icon = Icons.Default.Delete,
                isDestructive = true,
                onClick = onDelete
            )
        )
    }
}