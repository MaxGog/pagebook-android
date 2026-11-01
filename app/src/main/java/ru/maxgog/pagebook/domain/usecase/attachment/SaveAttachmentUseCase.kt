package ru.maxgog.pagebook.domain.usecase.attachment

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.maxgog.pagebook.data.file.AttachmentFileStore
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.AttachmentType
import ru.maxgog.pagebook.domain.model.OwnerType
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import javax.inject.Inject

class SaveAttachmentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AttachmentRepository,
    private val fileStore: AttachmentFileStore
) {
    suspend operator fun invoke(uri: Uri, ownerType: OwnerType, ownerId: Int): Attachment {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri) ?: "application/octet-stream"
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "bin"

        var name = "file.$ext"
        var size = 0L
        resolver.query(uri, null, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIdx = c.getColumnIndex(OpenableColumns.SIZE)
                if (nameIdx != -1) name = c.getString(nameIdx)
                if (sizeIdx != -1) size = c.getLong(sizeIdx)
            }
        }

        val localPath = resolver.openInputStream(uri)!!.use { input ->
            fileStore.save(input, ext)
        }

        val attachment = Attachment(
            ownerType = ownerType,
            ownerId = ownerId,
            type = when {
                mime.startsWith("image/") -> AttachmentType.IMAGE
                mime.startsWith("video/") -> AttachmentType.VIDEO
                mime.startsWith("audio/") -> AttachmentType.AUDIO
                else -> AttachmentType.FILE
            },
            localPath = localPath,
            mimeType = mime,
            displayName = name,
            sizeBytes = size
        )
        val id = repository.insertAttachment(attachment)
        return attachment.copy(id = id)
    }
}