package ru.maxgog.pagebook.domain.usecase.event

import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) = repository.deleteEvent(event)
}