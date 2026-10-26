package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.TaskModel
import ru.maxgog.pagebook.domain.model.Task

fun TaskModel.toDomain(): Task = Task(
    id = id, title = title, description = description,
    isCompleted = isCompleted, createdAt = createdAt,
    hasReminder = hasReminder, reminderTime = reminderTime,
    isAddedToCalendar = isAddedToCalendar, noteId = noteId
)

fun Task.toEntity(): TaskModel = TaskModel(
    id = id, title = title, description = description,
    isCompleted = isCompleted, createdAt = createdAt,
    hasReminder = hasReminder, reminderTime = reminderTime,
    isAddedToCalendar = isAddedToCalendar, noteId = noteId
)