package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.EventModel
import ru.maxgog.pagebook.data.models.EventWithTags
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.model.Tag

fun EventModel.toDomain(tags: List<Tag> = emptyList()): Event = Event(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    tags = tags
)

fun EventWithTags.toDomain(): Event =
    event.toDomain(tags = tags.map { it.toDomain() })

fun Event.toEntity(): EventModel = EventModel(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time
)