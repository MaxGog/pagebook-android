package ru.maxgog.pagebook.domain.usecase.note

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.repository.NoteRepository
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}