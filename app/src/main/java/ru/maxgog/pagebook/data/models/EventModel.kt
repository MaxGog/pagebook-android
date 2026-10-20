package ru.maxgog.pagebook.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import ru.maxgog.pagebook.Converters

import kotlinx.datetime.LocalDate

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class EventModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: LocalDate?,
    val time: String
)