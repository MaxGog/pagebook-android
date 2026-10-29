package ru.maxgog.pagebook.ui.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.ui.navigation.TaskEditDestination

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TaskListDetailPane(
    navController: NavHostController,
    onSettingsClick: () -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<TaskEditDestination>()
    val scope = rememberCoroutineScope()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            TaskListScreen(
                onAddTask = {
                    scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                },
                onTaskClick = { taskId ->
                    scope.launch {
                        navigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            TaskEditDestination(taskId)
                        )
                    }
                },
                onSettingsClick = onSettingsClick
            )
        },
        detailPane = {
            val destination = navigator.currentDestination?.contentKey
            if (destination != null) {
                TaskEditScreen(
                    taskId = destination.taskId,
                    onBack = {
                        scope.launch { navigator.navigateBack() }
                    }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Выберите задачу", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    )
}