package ru.maxgog.pagebook.ui.navigation

/**
 * Маршруты, не входящие в нижнюю панель навигации.
 * Для top-level экранов используйте [Screen].
 */

object Routes {
    const val NOTE_ADD = "note/add"
    const val NOTE_DETAIL = "note/{noteId}"
    const val ARG_NOTE_ID = "noteId"
    const val SETTINGS = "settings"

    fun noteDetail(id: Int) = "note/$id"
}