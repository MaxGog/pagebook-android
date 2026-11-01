package ru.maxgog.pagebook.ui.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.ImageData
import com.mohamedrejeb.richeditor.model.ImageLoader
import java.io.File

@OptIn(ExperimentalRichTextApi::class)
object AttachmentImageLoader : ImageLoader {
    @Composable
    override fun load(model: Any): ImageData? {
        val context = LocalContext.current
        val path = model as? String ?: return null

        val file = File(File(context.filesDir, "attachments"), path)
        if (!file.exists()) return null

        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        return ImageData(BitmapPainter(bitmap.asImageBitmap()))
    }
}