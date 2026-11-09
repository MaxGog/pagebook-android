package ru.maxgog.pagebook.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.domain.model.NotePlatform
import ru.maxgog.pagebook.domain.model.NoteVariant

interface NoteVariantRepository {
    fun getVariantsForNote(noteId: Int): Flow<List<NoteVariant>>
    suspend fun getVariant(noteId: Int, platform: NotePlatform): NoteVariant?
    suspend fun saveVariant(variant: NoteVariant): Long
    suspend fun deleteVariant(variant: NoteVariant)
}