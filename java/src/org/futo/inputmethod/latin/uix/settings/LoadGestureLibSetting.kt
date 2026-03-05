package org.futo.inputmethod.latin.uix.settings

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.futo.inputmethod.latin.common.FileUtils
import org.futo.inputmethod.latin.utils.ChecksumCalculator
import org.futo.inputmethod.latin.utils.JniUtils
import java.io.File

@Composable
fun LoadGestureLibSetting() {
    val ctx = LocalContext.current
    var showConfirmLoad by rememberSaveable { mutableStateOf(false) }
    var showChecksumWarning by rememberSaveable { mutableStateOf(false) }
    var pendingLibPath by rememberSaveable { mutableStateOf<String?>(null) }

    val abi = Build.SUPPORTED_ABIS[0]
    val libFile = File(ctx.filesDir, JniUtils.JNI_LIB_IMPORT_FILE_NAME)

    fun installLib(path: String) {
        val src = File(path)
        libFile.delete()
        src.copyTo(libFile, overwrite = true)
        libFile.setReadOnly()
        src.delete()
        Runtime.getRuntime().exit(0)
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        val tmpFile = File(ctx.filesDir, "tmpgesturelib")
        try {
            FileUtils.copyContentUriToNewFile(uri, ctx, tmpFile)
            pendingLibPath = tmpFile.absolutePath
            showChecksumWarning = true
        } catch (e: Exception) {
            tmpFile.delete()
        }
    }

    SettingItem(
        title = "Load gesture library ($abi)",
        subtitle = if (libFile.exists()) "Library loaded — tap to replace or remove"
                   else "Load libjni_latinimegoogle.so from Gboard",
        onClick = { showConfirmLoad = true },
        content = {}
    )

    if (showConfirmLoad) {
        AlertDialog(
            onDismissRequest = { showConfirmLoad = false },
            title = { Text("Load gesture library") },
            text = { Text("Select libjni_latinimegoogle.so for $abi extracted from a Gboard APK. The app will restart after loading.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmLoad = false
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("application/octet-stream")
                    filePicker.launch(intent)
                }) { Text("Choose file") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmLoad = false
                    if (libFile.exists()) {
                        libFile.delete()
                        Runtime.getRuntime().exit(0)
                    }
                }) { Text(if (libFile.exists()) "Remove library" else "Cancel") }
            }
        )
    }

    if (showChecksumWarning && pendingLibPath != null) {
        val checksum = ChecksumCalculator.checksum(File(pendingLibPath!!))
        AlertDialog(
            onDismissRequest = {
                showChecksumWarning = false
                File(pendingLibPath!!).delete()
                pendingLibPath = null
            },
            title = { Text("Unrecognized library") },
            text = { Text("SHA-256: $checksum\n\nThis library was not verified. Install anyway?") },
            confirmButton = {
                TextButton(onClick = {
                    showChecksumWarning = false
                    installLib(pendingLibPath!!)
                }) { Text("Install") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showChecksumWarning = false
                    File(pendingLibPath!!).delete()
                    pendingLibPath = null
                }) { Text("Cancel") }
            }
        )
    }
}
