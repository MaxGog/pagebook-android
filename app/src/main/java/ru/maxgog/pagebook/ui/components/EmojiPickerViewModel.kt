package ru.maxgog.pagebook.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.domain.repository.RecentEmojiRepository
import javax.inject.Inject

@HiltViewModel
class EmojiPickerViewModel @Inject constructor(
    private val recentEmojiRepository: RecentEmojiRepository
) : ViewModel() {

    val recentEmojis: StateFlow<List<String>> = recentEmojiRepository
        .getRecentEmojis()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onEmojiSelected(emoji: String) {
        viewModelScope.launch { recentEmojiRepository.addRecentEmoji(emoji) }
    }
}