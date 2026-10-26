package ru.maxgog.pagebook.data.mapper

import ru.maxgog.pagebook.data.models.EventModel
import ru.maxgog.pagebook.domain.model.Event

fun EventModel.toDomain(): Event = Event(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time
)

fun Event.toEntity(): EventModel = EventModel(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time
)