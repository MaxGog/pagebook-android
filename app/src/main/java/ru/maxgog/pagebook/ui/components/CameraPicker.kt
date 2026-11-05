package ru.maxgog.pagebook.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraPicker(
    onPicked: (Uri) -> Unit,
    onError: () -> Unit = {}
): () -> Unit {
    val context = LocalContext.current

    var pendingFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingFile
        pendingFile = null
        if (success) {
            file?.let {
                runCatching {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        it
                    )
                }.onSuccess(onPicked)
            }
        } else {
            file?.delete()
        }
    }

    return {
        try {
            val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            if (!file.exists()) file.createNewFile()
            pendingFile = file
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            launcher.launch(uri)
        } catch (e: Exception) {
            pendingFile?.delete()
            pendingFile = null
            onError()
        }
    }
}