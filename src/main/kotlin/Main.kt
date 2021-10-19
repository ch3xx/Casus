import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import compose.Colors
import kotlinx.coroutines.delay
import java.awt.FileDialog
import java.awt.Frame
import java.awt.Window
import java.io.File

@Composable
@Preview
fun CasusApp() {
    val selectedImage = painterResource("drawable/image_placeholder.png")
    val message = remember { mutableStateOf("") }
    val fileName = remember { mutableStateOf("") }
    val binaryImage = remember { mutableStateOf(false) }

    val isDialogOpen = remember { mutableStateOf(false) }

    if (isDialogOpen.value) {
        FileDialog(
            onCloseRequest = {
                it?.let { path ->
                    if (
                        path.endsWith(".png") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".jpeg")
                    ) {

                    }
                }
            }
        )
    }
    Row(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .width(470.dp)
                .padding(top = 4.dp, start = 8.dp)
        ) {
            Column {
                Image(selectedImage, "", modifier = Modifier.width(400.dp).height(450.dp))
                Button(
                    onClick = {
                        isDialogOpen.value = true
                    },
                    shape = RoundedCornerShape(size = 6.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
                    modifier = Modifier.width(400.dp)
                        .fillMaxHeight()
                        .padding(top = 16.dp)
                ) {
                    Text("Resim Seç", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxHeight()
                .width(730.dp)
                .padding(top = 4.dp, end = 8.dp)
        ) {
            TextField(
                message.value,
                {
                    message.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Resime gizlenecek mesaj")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(
                fileName.value,
                {
                    fileName.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Dosya ismi (.png)")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row {
                Checkbox(
                    checked = binaryImage.value,
                    {
                        binaryImage.value = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Colors.LIGHT_BLUE)
                )
                Text(
                    "Resmi Binary (siyah-beyaz) yap",
                    fontWeight = FontWeight.Medium,
                    color = Colors.BLUE,
                    modifier = Modifier.padding(top = 1.dp, start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun FileDialog(
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

fun main() = application {
    val icon = painterResource("CasusLogoLauncher.png")
    val logo = painterResource("CasusLogo.jpeg")
    val isPerformingTask = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(0)
        isPerformingTask.value = false
    }

    Tray(
        icon = icon,
        menu = {
            Item("Çık", onClick = ::exitApplication)
        }
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Casus",
        icon = icon,
        state = WindowState(
            width = 1200.dp,
            height = 600.dp,
        ),
        resizable = false
    ) {
        if (isPerformingTask.value) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            CasusApp()
        }
    }
}