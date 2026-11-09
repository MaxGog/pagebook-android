package ru.maxgog.pagebook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.data.dao.NoteVariantDao
import ru.maxgog.pagebook.data.mapper.toDomain
import ru.maxgog.pagebook.data.mapper.toEntity
import ru.maxgog.pagebook.domain.model.NotePlatform
import ru.maxgog.pagebook.domain.model.NoteVariant
import ru.maxgog.pagebook.domain.repository.NoteVariantRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteVariantRepositoryImpl @Inject constructor(
    private val dao: NoteVariantDao
) : NoteVariantRepository {

    override fun getVariantsForNote(noteId: Int): Flow<List<NoteVariant>> =
        dao.getForNote(noteId).map { list -> list.map { it.toDomain() } }

    override suspend fun getVariant(noteId: Int, platform: NotePlatform): NoteVariant? =
        dao.getByPlatform(noteId, platform.name)?.toDomain()

    override suspend fun saveVariant(variant: NoteVariant): Long =
        if (variant.id == 0) {
            dao.insert(variant.toEntity())
        } else {
            dao.update(variant.toEntity())
            variant.id.toLong()
        }

    override suspend fun deleteVariant(variant: NoteVariant) =
        dao.delete(variant.toEntity())
}