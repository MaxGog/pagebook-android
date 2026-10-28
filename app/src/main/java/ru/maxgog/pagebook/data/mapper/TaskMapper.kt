package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.TaskModel
import ru.maxgog.pagebook.data.models.TaskWithTags
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.Task

fun TaskModel.toDomain(tags: List<Tag> = emptyList()): Task = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = createdAt,
    hasReminder = hasReminder,
    reminderTime = reminderTime,
    isAddedToCalendar = isAddedToCalendar,
    noteId = noteId,
    tags = tags
)

fun TaskWithTags.toDomain(): Task =
    task.toDomain(tags = tags.map { it.toDomain() })

fun Task.toEntity(): TaskModel = TaskModel(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = createdAt,
    hasReminder = hasReminder,
    reminderTime = reminderTime,
    isAddedToCalendar = isAddedToCalendar,
    noteId = noteId
)