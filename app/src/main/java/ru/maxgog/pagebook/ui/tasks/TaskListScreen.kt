package ru.maxgog.pagebook.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import ru.maxgog.pagebook.domain.model.Task
import ru.maxgog.pagebook.ui.app.EmptyState
import ru.maxgog.pagebook.ui.components.ContextAction
import ru.maxgog.pagebook.ui.components.ContextualItem
import ru.maxgog.pagebook.ui.components.FilterBadgeIcon
import ru.maxgog.pagebook.ui.components.OverflowMenu
import ru.maxgog.pagebook.ui.components.TagFilterRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var filterRowVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(filterState.count) {
        if (filterState.count > 0) filterRowVisible = true
    }

    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.todo_list),
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
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        text = stringResource(R.string.add_task),
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
                    is TaskListUiState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is TaskListUiState.Error -> EmptyState(
                        text = state.message,
                        modifier = Modifier.fillMaxSize()
                    )

                    is TaskListUiState.Success -> {
                        if (state.tasks.isEmpty()) {
                            EmptyState(
                                text = if (filterState.isEmpty)
                                    stringResource(R.string.no_tasks)
                                else
                                    stringResource(R.string.no_matching_items),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 4.dp,
                                    bottom = 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.tasks, key = { it.id }) { task ->
                                    val actions = rememberTaskActions(
                                        task = task,
                                        onEdit = { onTaskClick(task.id) },
                                        onToggle = { viewModel.toggleCompleted(task) },
                                        onDelete = { taskToDelete = task }
                                    )
                                    ContextualItem(
                                        actions = actions,
                                        onClick = { onTaskClick(task.id) }
                                    ) {
                                        TaskItem(
                                            task = task,
                                            onToggle = { viewModel.toggleCompleted(task) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_task_confirm, task.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(task)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun rememberTaskActions(
    task: Task,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
): List<ContextAction> {
    val editLabel = stringResource(R.string.edit_task)
    val toggleLabel = stringResource(
        if (task.isCompleted) R.string.mark_uncompleted else R.string.mark_completed
    )
    val deleteLabel = stringResource(R.string.delete)
    return remember(task, editLabel, toggleLabel, deleteLabel) {
        listOf(
            ContextAction(
                label = editLabel,
                icon = Icons.Default.Edit,
                onClick = onEdit
            ),
            ContextAction(
                label = toggleLabel,
                icon = if (task.isCompleted)
                    Icons.Default.RadioButtonUnchecked
                else
                    Icons.Default.CheckCircle,
                onClick = onToggle
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