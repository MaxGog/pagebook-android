package ru.maxgog.pagebook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "ColorHex") val colorHex: String, // "#FF3B30" — ARGB в hex
    @ColumnInfo(name = "Emoji") val emoji: String? = null
)