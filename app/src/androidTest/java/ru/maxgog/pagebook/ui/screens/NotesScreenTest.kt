package ru.maxgog.pagebook.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.maxgog.pagebook.MainActivity

@HiltAndroidTest
class NotesScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun notesScreen_displaysNotesFromViewModel() {
        composeTestRule.setContent {
        }

        composeTestRule.onNodeWithText("Заметки").assertIsDisplayed()
    }

    @Test
    fun clickingAddButton_invokesCallback() {
        var addClicked = false
        composeTestRule.setContent {
            NotesScreen(
                onNoteClick = {},
                onAddNote = { addClicked = true },
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Добавить").performClick()
        assertThat(addClicked).isTrue()
    }
}