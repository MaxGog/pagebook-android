package ru.maxgog.pagebook.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.ui.graphics.vector.ImageVector
import ru.maxgog.pagebook.R

sealed class Screen(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelResId: Int
) {
    object Todo : Screen(
        route = "todo",
        selectedIcon = Icons.Default.Checklist,
        unselectedIcon = Icons.Outlined.Checklist,
        labelResId = R.string.todo_list
    )
    object Notes : Screen(
        route = "notes",
        selectedIcon = Icons.AutoMirrored.Filled.Note,
        unselectedIcon = Icons.AutoMirrored.Outlined.Note,
        labelResId = R.string.notes
    )
    object Calendar : Screen(
        route = "calendar",
        selectedIcon = Icons.Default.CalendarToday,
        unselectedIcon = Icons.Outlined.CalendarToday,
        labelResId = R.string.calendar
    )

    companion object {
        val bottomNavItems = listOf(Todo, Notes, Calendar)
    }
}