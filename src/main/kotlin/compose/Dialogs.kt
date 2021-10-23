package compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Dialog
import model.FileModel
import java.awt.FileDialog
import java.awt.FileDialog.LOAD
import java.awt.FileDialog.SAVE
import java.awt.Frame

@Composable
fun FileDialog(
    parent: Frame? = null,
    mode: Int,
    onCloseRequest: (result: FileModel) -> Unit
) = AwtWindow(
    create = {
        val title = when(mode) {
            SAVE -> "Kaydedilecek yeri seçin"
            LOAD -> "Bir resim seçin"
            else -> "Dosya"
        }
        object : FileDialog(parent, title, mode) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(FileModel(file, directory))
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@Composable
fun AlertDialog(onCloseRequest: () -> Unit, text: String) {
    Dialog(
        onCloseRequest = onCloseRequest,
        title = "Uyarı",
        resizable = false
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
                    .padding(bottom = 16.dp)
            )
            Button(
                onClick = onCloseRequest,
                modifier = Modifier.fillMaxWidth().height(64.dp).align(Alignment.BottomCenter).padding(8.dp),
                text = "Tamam"
            )
        }
    }
}