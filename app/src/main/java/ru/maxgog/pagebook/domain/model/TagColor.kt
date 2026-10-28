package ru.maxgog.pagebook.domain.model

enum class TagColor(val hex: String, val displayName: String) {
    RED("#FF3B30", "Красный"),
    ORANGE("#FF9500", "Оранжевый"),
    YELLOW("#FFCC00", "Жёлтый"),
    GREEN("#34C759", "Зелёный"),
    BLUE("#007AFF", "Синий"),
    PURPLE("#AF52DE", "Фиолетовый"),
    GRAY("#8E8E93", "Серый")
}