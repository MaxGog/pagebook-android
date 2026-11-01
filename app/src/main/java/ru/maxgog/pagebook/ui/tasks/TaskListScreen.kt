package ru.maxgog.pagebook.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ru.maxgog.pagebook.ui.app.EmptyState
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
    val selectedTagIds by viewModel.selectedTagIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.todo_list)) },
                actions = { OverflowMenu(onSettingsClick = onSettingsClick) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_task)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (allTags.isNotEmpty()) {
                TagFilterRow(
                    allTags = allTags,
                    selectedTagIds = selectedTagIds,
                    onToggleTag = viewModel::toggleTagFilter,
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
                                text = if (selectedTagIds.isEmpty())
                                    stringResource(R.string.no_tasks)
                                else
                                    stringResource(R.string.no_matching_items),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(state.tasks, key = { it.id }) { task ->
                                    TaskItem(
                                        task = task,
                                        onToggle = { viewModel.toggleCompleted(task) },
                                        onClick = { onTaskClick(task.id) }
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