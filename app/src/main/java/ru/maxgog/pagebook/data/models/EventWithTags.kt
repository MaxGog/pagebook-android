package ru.maxgog.pagebook.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EventWithTags(
    @Embedded val event: EventModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EventTagCrossRef::class,
            parentColumn = "eventId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagModel>
)