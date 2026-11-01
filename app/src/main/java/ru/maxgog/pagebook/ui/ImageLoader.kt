package ru.maxgog.pagebook.ui

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.ImageData
import com.mohamedrejeb.richeditor.model.ImageLoader
import java.io.File

@OptIn(ExperimentalRichTextApi::class)
object LocalAttachmentImageLoader : ImageLoader {
    @Composable
    override fun load(model: Any): ImageData? {
        val path = model as? String ?: return null
        val file = File(path)
        if (!file.exists()) return null
        val bitmap = remember(file.path) {
            BitmapFactory.decodeFile(file.absolutePath)
        } ?: return null
        return ImageData(bitmap.asImageBitmap() as Painter)
    }
}