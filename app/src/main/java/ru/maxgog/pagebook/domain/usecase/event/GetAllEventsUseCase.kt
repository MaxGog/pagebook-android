package ru.maxgog.pagebook.domain.usecase.event

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.Event
import ru.maxgog.pagebook.domain.repository.EventRepository
import javax.inject.Inject

class GetAllEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(): Flow<List<Event>> = repository.getAllEvents()
}