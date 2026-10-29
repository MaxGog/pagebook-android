package ru.maxgog.pagebook.domain.usecase.task

import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.tasks.await
import ru.maxgog.pagebook.domain.model.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateTaskFromNoteUseCase @Inject constructor() {

    private val extractor: EntityExtractor by lazy {
        EntityExtraction.getClient(
            EntityExtractorOptions.Builder(EntityExtractorOptions.RUSSIAN)
                .build()
        )
    }

    /**
     * Извлекает из текста заметки сущности типа DATE_TIME
     * и возвращает заготовку задачи с заполненным reminderTime.
     *
     * Если дату найти не удалось (модель не скачана, нет сети,
     * в тексте нет дат) — возвращает задачу без напоминания.
     */
    suspend operator fun invoke(noteContent: String, noteTitle: String): Task {
        val plainText = stripHtml(noteContent)
        val reminderTime = extractReminderTime(plainText)

        return Task(
            title = noteTitle.ifBlank { "Задача из заметки" },
            description = plainText.take(200).ifBlank { null },
            hasReminder = reminderTime != null,
            reminderTime = reminderTime
        )
    }

    private suspend fun extractReminderTime(text: String): Long? {
        if (text.isBlank()) return null

        return try {
            val params = EntityExtractionParams.Builder(text)
                .setEntityTypesFilter(setOf(Entity.TYPE_DATE_TIME))
                .build()

            val annotations = extractor.annotate(params).await()

            annotations
                .flatMap { it.entities }
                .filter { it.type == Entity.TYPE_DATE_TIME }
                .mapNotNull { entity ->
                    entity.asDateTimeEntity()?.timestampMillis
                }
                .filter { it > System.currentTimeMillis() }
                .minOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun stripHtml(html: String): String =
        html
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<[^>]*>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
}