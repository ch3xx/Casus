package compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Dialog
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Bir resim seçin", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory + file)
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@Composable
fun SaveFileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Kaydedilecek yeri seçin", SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory)
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