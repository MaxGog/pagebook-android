package ru.maxgog.pagebook.domain.usecase

import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}