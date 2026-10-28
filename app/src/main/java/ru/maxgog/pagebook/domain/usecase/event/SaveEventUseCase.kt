package ru.maxgog.pagebook.domain.usecase.event

import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.repository.EventRepository
import javax.inject.Inject

class SaveEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event): Long {
        return if (event.id == 0) {
            repository.insertEvent(event)
        } else {
            repository.updateEvent(event)
            event.id.toLong()
        }
    }
}