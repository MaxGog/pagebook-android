package ru.maxgog.pagebook.domain.usecase.note

import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        if (note.id == 0) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note)
        }
    }
}