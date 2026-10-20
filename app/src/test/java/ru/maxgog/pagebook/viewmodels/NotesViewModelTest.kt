package ru.maxgog.pagebook.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.maxgog.pagebook.data.models.NoteModel

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insert calls repository insert`() = runTest {
        val repository = mockk<NotesRepository>(relaxed = true)
        val viewModel = NotesViewModel(repository)
        val note = NoteModel(title = "Новая")

        viewModel.insert(note)

        coVerify(exactly = 1) { repository.insert(note) }
    }

    @Test
    fun `allNotes emits from repository`() = runTest {
        val testNotes = listOf(NoteModel(id = 1, title = "Заметка 1"))
        val repository = mockk<NotesRepository>()
        every { repository.allNotes } returns flowOf(testNotes)

        val viewModel = NotesViewModel(repository)

        viewModel.allNotes.test {
            val emittedList = awaitItem()
            assertThat(emittedList).isEqualTo(testNotes)
            awaitComplete()
        }
    }
}