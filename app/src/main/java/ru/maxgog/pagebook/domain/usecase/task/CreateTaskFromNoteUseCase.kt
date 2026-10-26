package ru.maxgog.pagebook.domain.usecase.task

import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import ru.maxgog.pagebook.domain.model.Task
import javax.inject.Inject

class CreateTaskFromNoteUseCase @Inject constructor() {

    /**
     * Извлекает из текста заметки сущности типа DATE_TIME
     * и возвращает заготовку задачи с заполненным reminderTime.
     */
    suspend operator fun invoke(noteContent: String, noteTitle: String): Task {
        val extractor = EntityExtraction.getClient(
            EntityExtractorOptions.Builder(EntityExtractorOptions.RUSSIAN)
                .build()
        )

        //val params = EntityExtractionParams.Builder(noteContent)
        //    .setEntityTypesFilter(listOf(EntityExtraction.EntityType.TYPE_DATE_TIME))
        //    .build()

        //val annotations: List<EntityAnnotation> = extractor.annotate(params).await()

        // Берём первую найденную дату (можно улучшить: выбирать самую позднюю/ближайшую)
        //val reminderTime = annotations
        //    .flatMap { it.entities }
        //    .firstOrNull { it.type == EntityExtraction.EntityType.TYPE_DATE_TIME }
        //    ?.timestampMillis

        return Task(
            title = noteTitle.ifBlank { "Задача из заметки" },
            description = noteContent.take(200),
        //    hasReminder = reminderTime != null,
        //    reminderTime = reminderTime
        )
    }
}