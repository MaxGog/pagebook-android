package ru.maxgog.pagebook.data.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentFileStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val root = File(context.filesDir, "attachments").apply { mkdirs() }

    /** Сохраняет InputStream в app-private storage, возвращает относительный путь. */
    suspend fun save(input: InputStream, extension: String): String = withContext(Dispatchers.IO) {
        val file = File(root, "${UUID.randomUUID()}.$extension")
        file.outputStream().use { input.copyTo(it) }
        file.relativeTo(root).path
    }

    fun open(localPath: String): File = File(root, localPath)

    fun delete(localPath: String) {
        File(root, localPath).delete()
    }
}