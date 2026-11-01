package ru.maxgog.pagebook.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.AttachmentType
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttachmentPreviewList(
    attachments: List<Attachment>,
    onRemove: (Attachment) -> Unit,
    onDownload: (Attachment) -> Unit,
    modifier: Modifier = Modifier
) {
    if (attachments.isEmpty()) return

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        attachments.forEach { a ->
            if (a.type == AttachmentType.IMAGE) {
                ImageThumb(
                    attachment = a,
                    onRemove = { onRemove(a) },
                    onDownload = { onDownload(a) }
                )
            } else {
                FileChip(
                    attachment = a,
                    onRemove = { onRemove(a) },
                    onDownload = { onDownload(a) }
                )
            }
        }
    }
}

@Composable
private fun ImageThumb(
    attachment: Attachment,
    onRemove: () -> Unit,
    onDownload: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(attachment.localPath) {
        val file = File(File(context.filesDir, "attachments"), attachment.localPath)
        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = attachment.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
        ) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.attachment_delete),
                    modifier = Modifier.padding(4.dp).size(16.dp)
                )
            }
        }

        IconButton(
            onClick = onDownload,
            modifier = Modifier.align(Alignment.BottomEnd).size(28.dp)
        ) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp).size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FileChip(
    attachment: Attachment,
    onRemove: () -> Unit,
    onDownload: () -> Unit
) {
    AssistChip(
        onClick = onDownload,
        label = { Text(attachment.displayName, maxLines = 1) },
        leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    )
}