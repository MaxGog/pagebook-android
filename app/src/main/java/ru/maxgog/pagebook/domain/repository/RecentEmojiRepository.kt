package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentEmojiRepository {
    fun getRecentEmojis(): Flow<List<String>>
    suspend fun addRecentEmoji(emoji: String)
}