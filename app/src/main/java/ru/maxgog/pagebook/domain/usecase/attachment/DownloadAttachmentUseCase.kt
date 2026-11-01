package ru.maxgog.pagebook.domain.usecase.attachment

import android.net.Uri
import ru.maxgog.pagebook.data.file.AttachmentFileStore
import ru.maxgog.pagebook.data.file.MediaStoreDownloader
import ru.maxgog.pagebook.domain.model.Attachment
import javax.inject.Inject

class DownloadAttachmentUseCase @Inject constructor(
    private val fileStore: AttachmentFileStore,
    private val downloader: MediaStoreDownloader
) {
    suspend operator fun invoke(attachment: Attachment): Uri {
        val file = fileStore.open(attachment.localPath)
        return downloader.download(attachment, file)
    }
}