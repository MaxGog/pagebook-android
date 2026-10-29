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
import ru.maxgog.pagebook.ui.EmptyState
import ru.maxgog.pagebook.ui.components.OverflowMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.todo_list)) },
                actions = {
                    OverflowMenu(onSettingsClick = onSettingsClick)
                }
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
        when (val state = uiState) {
            is TaskListUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is TaskListUiState.Error -> EmptyState(
                text = state.message,
                modifier = Modifier.padding(innerPadding)
            )

            is TaskListUiState.Success -> {
                if (state.tasks.isEmpty()) {
                    EmptyState(
                        text = stringResource(R.string.no_tasks),
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding,
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