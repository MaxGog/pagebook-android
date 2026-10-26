package ru.maxgog.pagebook.ui.navigation

import kotlinx.serialization.Serializable

// Главные экраны (для нижней навигации)
@Serializable
data object TaskListDestination

@Serializable
data class TaskEditDestination(val taskId: Int? = null)
@Serializable
data object NotesDestination

@Serializable
data object CalendarDestination

// Второстепенные экраны
@Serializable
data object NoteAddDestination

@Serializable
data class NoteDetailDestination(val noteId: Int)

@Serializable
data object SettingsDestination