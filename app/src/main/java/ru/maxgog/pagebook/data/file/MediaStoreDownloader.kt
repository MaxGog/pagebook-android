package ru.maxgog.pagebook.data.file

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.AttachmentType
import java.io.File
import javax.inject.Inject

class MediaStoreDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun download(attachment: Attachment, file: File): Uri = withContext(Dispatchers.IO) {
        val collection = when (attachment.type) {
            AttachmentType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            AttachmentType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            AttachmentType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            AttachmentType.FILE -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, attachment.displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, attachment.mimeType)
            if (attachment.type == AttachmentType.FILE) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = context.contentResolver.insert(collection, values)
            ?: throw IllegalStateException("Failed to create MediaStore entry")

        context.contentResolver.openOutputStream(uri)?.use { out ->
            file.inputStream().use { it.copyTo(out) }
        }
        uri
    }
}